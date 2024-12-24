package org.nstep.engine.framework.env.core.fegin;

import cn.hutool.core.util.StrUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.nstep.engine.framework.env.core.context.EnvContextHolder;
import org.nstep.engine.framework.env.core.util.EnvUtils;

/**
 * 多环境的 {@link RequestInterceptor} 实现类：Feign 请求时，将 tag 设置到 header 中，继续透传给被调用的服务
 * <p>
 * 该拦截器在 Feign 请求过程中，将当前环境的 tag 信息添加到请求头中，
 * 以便被调用的服务能够接收到该环境标签，从而进行相应的处理。
 */
public class EnvRequestInterceptor implements RequestInterceptor {

    /**
     * 在请求发送前，拦截并修改请求模板，添加当前环境的 tag 信息到请求头中。
     *
     * @param requestTemplate Feign 请求模板，包含请求的所有信息（如 URL、请求头等）
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 获取当前环境的 tag，可能来自于线程本地存储或其他上下文
        String tag = EnvContextHolder.getTag();

        // 如果 tag 不为空，则将其设置到请求头中
        if (StrUtil.isNotEmpty(tag)) {
            // 使用工具类将 tag 添加到请求的 header 中
            EnvUtils.setTag(requestTemplate, tag);
        }
    }
}
