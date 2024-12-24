package org.nstep.engine.framework.xss.core.clean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

/**
 * 基于 Jsoup 实现的 XSS 清理器，使用 Jsoup 库对 HTML 字符串进行 XSS 过滤。
 * <p>
 * 该类通过构建一个安全的标签和属性列表（Safelist）来过滤潜在的 XSS 攻击，防止恶意代码注入。
 * 它通过 Jsoup 的 `clean` 方法清理输入的 HTML 内容，确保只允许安全的标签和属性。
 */
public class JsoupXssCleaner implements XssCleaner {

    // 用于定义哪些标签和属性是允许的安全内容
    private final Safelist safelist;

    /**
     * 用于在处理相对路径时，强制将其转换为绝对路径的前缀。
     * <p>
     * 如果为空，则不会处理相对路径。值应为绝对路径的前缀（包括协议部分，如 "<a href=http://example.com></a>）。
     */
    private final String baseUri;

    /**
     * 无参构造函数，默认使用 {@link JsoupXssCleaner#buildSafelist} 方法构建一个安全的标签和属性列表。
     * 默认情况下，`baseUri` 为空，表示不处理相对路径。
     */
    public JsoupXssCleaner() {
        this.safelist = buildSafelist(); // 构建一个默认的安全列表
        this.baseUri = ""; // 默认不处理相对路径
    }

    /**
     * 构建一个 XSS 清理的 Safelist 规则。
     * <p>
     * 基于 {@link Safelist#relaxed()} 方法的基础上，进行了以下扩展：
     * 1. 允许所有标签拥有 `style` 和 `class` 属性，便于支持富文本样式。
     * 2. 允许 `a` 标签具有 `target` 属性，以支持链接的打开方式（例如，`_blank`）。
     * 3. 允许 `img` 标签的 `src` 属性支持 `data` 协议，以便支持 base64 编码的图像。
     *
     * @return 构建好的 {@link Safelist} 对象，包含 XSS 过滤规则。
     */
    private Safelist buildSafelist() {
        // 获取 Jsoup 提供的默认放宽版安全列表
        Safelist relaxedSafelist = Safelist.relaxed();

        // 扩展所有标签的属性，允许使用 style 和 class 属性
        relaxedSafelist.addAttributes(":all", "style", "class");

        // 允许 a 标签具有 target 属性，支持打开链接的方式设置
        relaxedSafelist.addAttributes("a", "target");

        // 允许 img 标签的 src 属性使用 data 协议，支持 base64 图像数据
        relaxedSafelist.addProtocols("img", "src", "data");

        // 返回构建好的安全列表
        return relaxedSafelist;
    }

    /**
     * 清理 HTML 字符串，移除不安全的标签和属性，防止 XSS 攻击。
     * <p>
     * 该方法使用 Jsoup 的 `clean` 方法清理输入的 HTML 内容，确保输出的内容符合安全标准。
     *
     * @param html 需要清理的 HTML 字符串。
     * @return 清理后的 HTML 字符串，已过滤掉不安全的标签和属性。
     */
    @Override
    public String clean(String html) {
        // 使用 Jsoup.clean 方法进行清理，传入 baseUri 和 safelist 规则
        return Jsoup.clean(html, baseUri, safelist, new Document.OutputSettings().prettyPrint(false));
    }

}
