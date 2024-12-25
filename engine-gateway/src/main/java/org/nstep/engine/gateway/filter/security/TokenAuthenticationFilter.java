package org.nstep.engine.gateway.filter.security;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.nstep.engine.framework.common.core.KeyValue;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.date.LocalDateTimeUtils;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.gateway.util.SecurityFrameworkUtils;
import org.nstep.engine.gateway.util.WebFrameworkUtils;
import org.nstep.engine.module.system.api.oauth2.OAuth2TokenApi;
import org.nstep.engine.module.system.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import static org.nstep.engine.framework.common.util.cache.CacheUtils.buildAsyncReloadingCache;

/**
 * Token 过滤器，用于验证 token 的有效性
 * <p>
 * 1. 如果 token 验证通过，将 userId、userType、tenantId 等信息通过 Header 转发给后端服务。
 * 2. 如果 token 验证不通过，依然会转发请求给后端服务。是否需要登录校验交由后端服务自行处理。
 * <p>
 * 该过滤器用于处理基于 token 的认证机制，确保只有合法用户能够访问受保护的资源。
 */
@Component
public class TokenAuthenticationFilter implements GlobalFilter, Ordered {

    /**
     * 用于解析 checkToken 的结果，表示 OAuth2 访问令牌验证结果的类型。
     */
    private static final TypeReference<CommonResult<OAuth2AccessTokenCheckRespDTO>> CHECK_RESULT_TYPE_REFERENCE
            = new TypeReference<>() {
    };

    /**
     * 空的 LoginUser 对象
     * <p>
     * 用于处理以下情况：
     * 1. 当 {@link #getLoginUser(ServerWebExchange, String)} 返回 Mono.empty() 时，避免后续 flatMap 无法处理。
     * 2. 当 Token 已经过期时，返回一个空的 LoginUser 对象，避免缓存无法刷新。
     */
    private static final LoginUser LOGIN_USER_EMPTY = new LoginUser();

    private final WebClient webClient;

    /**
     * 登录用户的本地缓存
     * <p>
     * 使用多租户编号和访问令牌作为缓存的键，缓存登录用户信息。
     */
    private final LoadingCache<KeyValue<Long, String>, LoginUser> loginUserCache = buildAsyncReloadingCache(Duration.ofMinutes(1),
            new CacheLoader<>() {

                @Override
                public LoginUser load(KeyValue<Long, String> token) {
                    String body = checkAccessToken(token.getKey(), token.getValue()).block();
                    return buildUser(body);
                }

            });

    /**
     * 构造函数，初始化 WebClient 并配置负载均衡功能
     * <p>
     * 由于 Spring Cloud OpenFeign 不支持 Reactive，因此使用 WebClient 代替。
     * 同时通过 ReactorLoadBalancerExchangeFilterFunction 实现负载均衡。
     */
    public TokenAuthenticationFilter(ReactorLoadBalancerExchangeFilterFunction lbFunction) {
        this.webClient = WebClient.builder().filter(lbFunction).build();
    }

    /**
     * 过滤器的核心方法，处理请求中的 Token 验证逻辑
     * <p>
     * 1. 如果请求中没有 Token，则直接继续执行后续的过滤器链。
     * 2. 如果请求中有 Token，则解析对应的用户信息，并将其设置到请求头中转发给后端服务。
     * <p>
     * 通过 Mono.empty() 来保证即使用户不存在，过滤器也能继续执行，避免返回空响应。
     *
     * @param exchange 请求和响应的交换对象
     * @param chain    过滤器链
     * @return Mono<Void> 返回异步执行的结果
     */
    @Override
    public Mono<Void> filter(final ServerWebExchange exchange, GatewayFilterChain chain) {
        // 移除 login-user 的请求头，避免伪造模拟
        SecurityFrameworkUtils.removeLoginUser(exchange);

        // 情况一：如果没有 Token 令牌，则直接继续过滤链
        String token = SecurityFrameworkUtils.obtainAuthorization(exchange);
        if (StrUtil.isEmpty(token)) {
            return chain.filter(exchange);
        }

        // 情况二：如果有 Token 令牌，则解析对应的 userId、userType、tenantId 等字段，并通过 Header 转发给服务
        return getLoginUser(exchange, token).defaultIfEmpty(LOGIN_USER_EMPTY).flatMap(user -> {
            // 1. 如果没有用户信息，直接继续过滤链
            if (user == LOGIN_USER_EMPTY || user.getExpiresTime() == null || LocalDateTimeUtils.beforeNow(user.getExpiresTime())) {
                return chain.filter(exchange);
            }

            // 2. 有用户信息，则设置登录用户，并将用户信息通过请求头传递给后端服务
            SecurityFrameworkUtils.setLoginUser(exchange, user);
            ServerWebExchange newExchange = exchange.mutate()
                    .request(builder -> SecurityFrameworkUtils.setLoginUserHeader(builder, user)).build();
            return chain.filter(newExchange);
        });
    }

    /**
     * 获取登录用户信息，首先尝试从缓存中获取，如果缓存中没有，则从远程服务获取
     *
     * @param exchange 请求交换对象
     * @param token    访问令牌
     * @return Mono<LoginUser> 返回登录用户的 Mono 对象
     */
    private Mono<LoginUser> getLoginUser(ServerWebExchange exchange, String token) {
        // 从缓存中获取 LoginUser
        Long tenantId = WebFrameworkUtils.getTenantId(exchange);
        KeyValue<Long, String> cacheKey = new KeyValue<>();
        cacheKey.setKey(tenantId);
        cacheKey.setValue(token);
        LoginUser localUser = loginUserCache.getIfPresent(cacheKey);
        if (localUser != null) {
            return Mono.just(localUser);
        }

        // 缓存不存在，则请求远程服务
        return checkAccessToken(tenantId, token).flatMap((Function<String, Mono<LoginUser>>) body -> {
            LoginUser remoteUser = buildUser(body);
            if (remoteUser != null) {
                // 非空，则缓存用户信息
                loginUserCache.put(cacheKey, remoteUser);
                return Mono.just(remoteUser);
            }
            return Mono.empty();
        });
    }

    /**
     * 校验访问令牌的有效性
     *
     * @param tenantId 租户编号
     * @param token    访问令牌
     * @return Mono<String> 返回验证结果的 Mono 对象
     */
    private Mono<String> checkAccessToken(Long tenantId, String token) {
        return webClient.get()
                .uri(OAuth2TokenApi.URL_CHECK, uriBuilder -> uriBuilder.queryParam("accessToken", token).build())
                .headers(httpHeaders -> WebFrameworkUtils.setTenantIdHeader(tenantId, httpHeaders)) // 设置租户的 Header
                .retrieve().bodyToMono(String.class);
    }

    /**
     * 根据返回的字符串结果构建 LoginUser 对象
     *
     * @param body 返回的结果字符串
     * @return LoginUser 构建的登录用户对象
     */
    private LoginUser buildUser(String body) {
        CommonResult<OAuth2AccessTokenCheckRespDTO> result = JsonUtils.parseObject(body, CHECK_RESULT_TYPE_REFERENCE);
        if (result == null || result.isError()) {
            // 如果结果为空或错误，返回 null 或 LOGIN_USER_EMPTY
            if (Objects.equals(result.getCode(), HttpStatus.UNAUTHORIZED.value())) {
                return LOGIN_USER_EMPTY;
            }
            return null;
        }

        // 创建并返回 LoginUser 对象
        OAuth2AccessTokenCheckRespDTO tokenInfo = result.getData();
        LoginUser loginUser = new LoginUser();
        loginUser.setId(tokenInfo.getUserId());
        loginUser.setUserType(tokenInfo.getUserType());
        loginUser.setInfo(tokenInfo.getUserInfo());
        loginUser.setTenantId(tokenInfo.getTenantId());
        loginUser.setScopes(tokenInfo.getScopes());
        loginUser.setExpiresTime(tokenInfo.getExpiresTime());
        return loginUser;
    }

    /**
     * 获取过滤器的执行顺序
     * <p>
     * 该顺序用于与 Spring Security 的过滤器顺序对齐。
     *
     * @return int 返回过滤器的顺序
     */
    @Override
    public int getOrder() {
        return -100; // 和 Spring Security Filter 的顺序对齐
    }

}
