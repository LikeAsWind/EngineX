package org.nstep.engine.framework.xss.core.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.nstep.engine.framework.xss.config.XssProperties;
import org.nstep.engine.framework.xss.core.clean.XssCleaner;
import org.springframework.util.PathMatcher;

import java.io.IOException;

/**
 * XSS 过滤 Jackson 反序列化器。
 * <p>
 * 在 JSON 反序列化过程中，对字符串类型的值进行 XSS 清理，确保反序列化后的数据安全。
 * 该类继承自 Jackson 提供的 `StringDeserializer`，并扩展了其功能。
 */
@Slf4j
@AllArgsConstructor
public class XssStringJsonDeserializer extends StringDeserializer {

    /**
     * XSS 配置属性。
     * 包含是否启用 XSS 过滤及需要排除的 URL 等配置信息。
     */
    private final XssProperties properties;

    /**
     * 路径匹配器，用于判断当前请求是否匹配排除的 URL。
     */
    private final PathMatcher pathMatcher;

    /**
     * XSS 清理器，用于对字符串进行 XSS 清理。
     */
    private final XssCleaner xssCleaner;

    /**
     * 对 JSON 中的字符串值进行反序列化，并执行 XSS 清理。
     *
     * @param p    JSON 解析器
     * @param text 反序列化上下文
     * @return 清理后的字符串
     * @throws IOException 如果解析或清理过程中发生错误
     */
    @Override
    public String deserialize(JsonParser p, DeserializationContext text) throws IOException {
        // 1. 检查是否匹配白名单 URL，如果匹配则直接返回原始值。
        HttpServletRequest request = ServletUtils.getRequest();
        if (request != null) {
            String uri = request.getRequestURI();
            if (properties.getExcludeUrls().stream().anyMatch(excludeUrl -> pathMatcher.match(excludeUrl, uri))) {
                return p.getText(); // 不进行 XSS 清理
            }
        }

        // 2. 如果是字符串类型的 JSON 值，使用 XSS 清理器进行清理。
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            return xssCleaner.clean(p.getText());
        }

        // 3. 处理其他 JSON Token 类型的情况。
        JsonToken t = p.currentToken();
        if (t == JsonToken.START_ARRAY) {
            // 如果是数组，调用父类的数组处理逻辑。
            return _deserializeFromArray(p, text);
        }

        if (t == JsonToken.VALUE_EMBEDDED_OBJECT) {
            // 处理嵌入的对象（例如 base64 编码的字节数组）。
            Object ob = p.getEmbeddedObject();
            if (ob == null) {
                return null;
            }
            if (ob instanceof byte[]) {
                return text.getBase64Variant().encode((byte[]) ob, false);
            }
            return ob.toString();
        }

        if (t == JsonToken.START_OBJECT) {
            // 如果是对象，尝试提取标量值。
            return text.extractScalarFromObject(p, this, _valueClass);
        }

        if (t.isScalarValue()) {
            // 处理其他标量值类型。
            String valueAsString = p.getValueAsString();
            return xssCleaner.clean(valueAsString);
        }

        // 如果遇到意外的 Token 类型，调用上下文的异常处理逻辑。
        return (String) text.handleUnexpectedToken(_valueClass, p);
    }
}
