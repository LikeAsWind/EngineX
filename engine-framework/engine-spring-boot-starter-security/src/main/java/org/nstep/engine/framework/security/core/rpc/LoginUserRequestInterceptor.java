package org.nstep.engine.framework.security.core.rpc;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.security.core.LoginUser;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * LoginUser 的 RequestInterceptor 实现类：Feign 请求时，将 {@link LoginUser} 设置到 header 中，继续透传给被调用的服务
 */
@Slf4j
public class LoginUserRequestInterceptor implements RequestInterceptor {

    /**
     * 拦截 Feign 请求，在请求头中添加 LoginUser 信息。
     * 该方法会在每次 Feign 请求时被调用，将当前登录用户的 {@link LoginUser} 对象序列化并添加到请求头中，传递给被调用的服务。
     *
     * @param requestTemplate Feign 请求模板
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 获取当前登录的用户
        LoginUser user = SecurityFrameworkUtils.getLoginUser();

        // 如果没有登录用户，则不做任何操作
        if (user == null) {
            return;
        }

        try {
            // 将 LoginUser 对象序列化为 JSON 字符串
            String userStr = JsonUtils.toJsonString(user);
            // 对序列化后的字符串进行 URL 编码，避免中文乱码
            userStr = URLEncoder.encode(userStr, StandardCharsets.UTF_8);

            // 将编码后的 LoginUser 信息设置到请求头中，透传给被调用的服务
            requestTemplate.header(SecurityFrameworkUtils.LOGIN_USER_HEADER, userStr);
        } catch (Exception ex) {
            // 如果序列化或编码过程中出现异常，记录错误日志并抛出异常
            log.error("[apply][序列化 LoginUser({}) 发生异常]", user, ex);
            throw ex;
        }
    }

}
