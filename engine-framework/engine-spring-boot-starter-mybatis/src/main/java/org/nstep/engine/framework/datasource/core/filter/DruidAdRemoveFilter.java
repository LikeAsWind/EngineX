package org.nstep.engine.framework.datasource.core.filter;

import com.alibaba.druid.util.Utils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Druid 底部广告过滤器
 * <p>
 * 该过滤器用于去除 Druid Web 监控页面底部的广告信息，主要是通过修改 `common.js` 文件来实现。
 * </p>
 */
public class DruidAdRemoveFilter extends OncePerRequestFilter {

    /**
     * common.js 的路径
     * <p>
     * 该路径指定了 `common.js` 文件的位置，文件中包含了 Druid Web 监控页面的广告信息。
     * </p>
     */
    private static final String COMMON_JS_ILE_PATH = "support/http/resources/js/common.js";

    /**
     * 过滤请求并移除广告信息
     * <p>
     * 该方法会在请求响应时被调用，主要用于修改响应内容，移除广告信息。
     * </p>
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param chain    过滤器链
     * @throws ServletException 如果过滤器链中出现异常
     * @throws IOException      如果发生 I/O 错误
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // 继续执行过滤器链中的其他过滤器
        chain.doFilter(request, response);

        // 重置响应缓冲区，确保后续操作不会影响响应头
        response.resetBuffer();

        // 获取 common.js 文件内容
        String text = Utils.readFromResource(COMMON_JS_ILE_PATH);

        // 使用正则表达式移除广告信息
        // 移除底部的广告链接（banner）
        text = text.replaceAll("<a.*?banner\"></a><br/>", "");
        // 移除包含 "powered" 字样的广告信息
        text = text.replaceAll("powered.*?shrek.wang</a>", "");

        // 将修改后的内容写入响应
        response.getWriter().write(text);
    }

}
