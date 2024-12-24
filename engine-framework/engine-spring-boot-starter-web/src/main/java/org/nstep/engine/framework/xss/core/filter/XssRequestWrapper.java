package org.nstep.engine.framework.xss.core.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.nstep.engine.framework.xss.core.clean.XssCleaner;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * XSS 请求包装器 (XssRequestWrapper)
 * <p>
 * 该类继承自 `HttpServletRequestWrapper`，用于包装 HTTP 请求对象。
 * 在请求的参数、属性、头信息以及查询字符串中自动进行 XSS 清理，确保请求数据的安全性。
 */
public class XssRequestWrapper extends HttpServletRequestWrapper {

    /**
     * XSS 清理器，用于对请求中的数据进行 XSS 清理。
     */
    private final XssCleaner xssCleaner;

    /**
     * 构造函数，创建一个新的 XSS 请求包装器。
     *
     * @param request    原始 HTTP 请求对象
     * @param xssCleaner XSS 清理器
     */
    public XssRequestWrapper(HttpServletRequest request, XssCleaner xssCleaner) {
        super(request);
        this.xssCleaner = xssCleaner;
    }

    // ============================ Parameter ============================

    /**
     * 获取请求参数的 Map，并对参数值进行 XSS 清理。
     *
     * @return 包含清理后参数值的 Map
     */
    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> map = new LinkedHashMap<>();
        Map<String, String[]> parameters = super.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameters.entrySet()) {
            String[] values = entry.getValue();
            for (int i = 0; i < values.length; i++) {
                values[i] = xssCleaner.clean(values[i]); // 清理每个参数值
            }
            map.put(entry.getKey(), values);
        }
        return map;
    }

    /**
     * 获取指定参数的值数组，并对每个值进行 XSS 清理。
     *
     * @param name 参数名称
     * @return 清理后的参数值数组
     */
    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        int count = values.length;
        String[] encodedValues = new String[count];
        for (int i = 0; i < count; i++) {
            encodedValues[i] = xssCleaner.clean(values[i]); // 清理每个值
        }
        return encodedValues;
    }

    /**
     * 获取指定参数的值，并对值进行 XSS 清理。
     *
     * @param name 参数名称
     * @return 清理后的参数值
     */
    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        if (value == null) {
            return null;
        }
        return xssCleaner.clean(value); // 清理值
    }

    // ============================ Attribute ============================

    /**
     * 获取指定属性的值，并对字符串类型的值进行 XSS 清理。
     *
     * @param name 属性名称
     * @return 清理后的属性值
     */
    @Override
    public Object getAttribute(String name) {
        Object value = super.getAttribute(name);
        if (value instanceof String) {
            return xssCleaner.clean((String) value); // 清理字符串类型的属性值
        }
        return value;
    }

    // ============================ Header ============================

    /**
     * 获取指定头信息的值，并对值进行 XSS 清理。
     *
     * @param name 头信息名称
     * @return 清理后的头信息值
     */
    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        if (value == null) {
            return null;
        }
        return xssCleaner.clean(value); // 清理头信息值
    }

    // ============================ Query String ============================

    /**
     * 获取查询字符串，并对其进行 XSS 清理。
     *
     * @return 清理后的查询字符串
     */
    @Override
    public String getQueryString() {
        String value = super.getQueryString();
        if (value == null) {
            return null;
        }
        return xssCleaner.clean(value); // 清理查询字符串
    }
}
