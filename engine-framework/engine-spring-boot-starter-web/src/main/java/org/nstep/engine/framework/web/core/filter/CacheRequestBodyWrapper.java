package org.nstep.engine.framework.web.core.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Request Body 缓存 Wrapper，包装 HTTP 请求体以实现可重复读取。
 * <p>
 * 该类继承自 `HttpServletRequestWrapper`，用于缓存 HTTP 请求的内容。默认情况下，HTTP 请求体只能读取一次，
 * 读取后会被消耗掉。该包装器通过缓存请求体的字节数组，使得请求体可以多次读取。
 */
public class CacheRequestBodyWrapper extends HttpServletRequestWrapper {

    /**
     * 缓存的请求体内容
     */
    private final byte[] body;

    /**
     * 构造函数，缓存请求体内容。
     * <p>
     * 通过调用 `ServletUtils.getBodyBytes(request)` 获取请求体的字节内容，并将其存储在 `body` 字节数组中。
     *
     * @param request 当前的 HTTP 请求
     */
    public CacheRequestBodyWrapper(HttpServletRequest request) {
        super(request);
        body = ServletUtils.getBodyBytes(request);
    }

    /**
     * 返回请求体内容的字符输入流。
     * <p>
     * 该方法通过 `InputStreamReader` 和 `BufferedReader` 将请求体的字节内容转换为字符输入流，
     * 使得可以像读取文本一样读取请求体内容。
     *
     * @return 请求体的字符输入流
     */
    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }

    /**
     * 返回请求体内容的字节输入流。
     * <p>
     * 该方法通过 `ByteArrayInputStream` 将缓存的字节内容转换为字节输入流，允许多次读取请求体内容。
     *
     * @return 请求体的字节输入流
     */
    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);
        // 返回 ServletInputStream
        return new ServletInputStream() {

            @Override
            public int read() {
                return inputStream.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int available() {
                return body.length;
            }

        };
    }

}
