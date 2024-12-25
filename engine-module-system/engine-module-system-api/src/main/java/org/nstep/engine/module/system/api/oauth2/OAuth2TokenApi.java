package org.nstep.engine.module.system.api.oauth2;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import org.nstep.engine.module.system.api.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import org.nstep.engine.module.system.api.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * OAuth2 令牌相关的 RPC 服务接口
 * <p>
 * 该接口提供了创建、校验、移除和刷新 OAuth2 访问令牌的功能，主要用于 OAuth2 认证与授权流程。
 * 各种操作均通过 HTTP 请求进行调用，支持 Feign 客户端进行远程调用。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - OAuth2.0 令牌")
public interface OAuth2TokenApi {

    // 定义 OAuth2 令牌相关的接口路径前缀
    String PREFIX = ApiConstants.PREFIX + "/oauth2/token";

    /**
     * 校验 Token 的 URL 地址，主要是提供给 Gateway 使用
     */
    @SuppressWarnings("HttpUrlsUsage")
    String URL_CHECK = "http://" + ApiConstants.NAME + PREFIX + "/check";

    /**
     * 创建访问令牌
     * <p>
     * 该方法用于根据传入的请求数据创建 OAuth2 访问令牌。返回的令牌可以用于访问受保护的资源。
     *
     * @param reqDTO 请求数据，包括用户信息、客户端编号和授权范围等
     * @return 返回创建的 OAuth2 访问令牌信息
     */
    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建访问令牌")
    CommonResult<OAuth2AccessTokenRespDTO> createAccessToken(@Valid @RequestBody OAuth2AccessTokenCreateReqDTO reqDTO);

    /**
     * 校验访问令牌的有效性
     * <p>
     * 该方法用于校验传入的访问令牌是否有效，通常用于验证用户的认证状态。
     *
     * @param accessToken 需要校验的访问令牌
     * @return 返回校验结果，包括用户信息和授权范围等
     */
    @GetMapping(PREFIX + "/check")
    @Operation(summary = "校验访问令牌")
    @Parameter(name = "accessToken", description = "访问令牌", required = true, example = "tudou")
    CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(@RequestParam("accessToken") String accessToken);

    /**
     * 移除访问令牌
     * <p>
     * 该方法用于移除指定的访问令牌，使其失效。通常用于用户注销或令牌过期等场景。
     *
     * @param accessToken 需要移除的访问令牌
     * @return 返回移除后的访问令牌信息
     */
    @DeleteMapping(PREFIX + "/remove")
    @Operation(summary = "移除访问令牌")
    @Parameter(name = "accessToken", description = "访问令牌", required = true, example = "tudou")
    CommonResult<OAuth2AccessTokenRespDTO> removeAccessToken(@RequestParam("accessToken") String accessToken);

    /**
     * 刷新访问令牌
     * <p>
     * 该方法用于根据提供的刷新令牌和客户端编号来刷新访问令牌。通常用于延长用户的登录状态。
     *
     * @param refreshToken 刷新令牌
     * @param clientId     客户端编号
     * @return 返回刷新后的访问令牌信息
     */
    @PutMapping(PREFIX + "/refresh")
    @Operation(summary = "刷新访问令牌")
    @Parameters({
            @Parameter(name = "refreshToken", description = "刷新令牌", required = true, example = "haha"),
            @Parameter(name = "clientId", description = "客户端编号", required = true, example = "engine")
    })
    CommonResult<OAuth2AccessTokenRespDTO> refreshAccessToken(@RequestParam("refreshToken") String refreshToken,
                                                              @RequestParam("clientId") String clientId);

}
