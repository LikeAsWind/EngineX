package org.nstep.engine.framework.xss.core.clean;

/**
 * 定义了一个用于清理 HTML 文本中 XSS 风险的接口。
 * <p>
 * 该接口用于对 HTML 内容进行 XSS 清理，移除潜在的恶意脚本或注入内容，确保返回的 HTML 内容是安全的。
 * 实现该接口的类需要提供具体的清理逻辑。
 */
public interface XssCleaner {

    /**
     * 清理 HTML 文本中的 XSS 风险内容。
     * <p>
     * 该方法的目的是扫描 HTML 内容，并移除或替换其中可能存在的恶意脚本、注入代码或不安全的标签与属性。
     * 它的返回值是经过清理后的 HTML 内容，确保其中不包含 XSS 攻击的风险。
     *
     * @param html 原始的 HTML 文本，需要进行 XSS 清理。
     * @return 清理后的 HTML 文本，已移除所有潜在的 XSS 风险。
     */
    String clean(String html);
}
