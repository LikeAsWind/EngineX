package org.nstep.engine.module.message.dto.model;

import lombok.*;

/**
 * 短信信息模型
 * <p>
 * 该类表示短信内容的模型，继承自 `ContentModel` 类。它包含了短信的文本内容和链接信息，主要用于构建发送短信的内容。
 * </p>
 */

@EqualsAndHashCode(callSuper = true)
@Data  // 自动生成 getter、setter、toString、equals 和 hashCode 方法
@Builder  // 提供构建者模式，方便创建对象
@AllArgsConstructor  // 自动生成带有所有字段的构造函数
@NoArgsConstructor  // 自动生成无参构造函数
public class SmsContentModel extends ContentModel {

    /**
     * 文本
     * <p>
     * 该字段表示短信的文本内容，用于存储要发送的短信内容。
     * </p>
     */
    private String content;

    /**
     * 链接 主要用于长链转短链
     * <p>
     * 该字段用于存储短信中的链接，通常用于将长链接转换为短链接，以节省字符数并提高用户体验。
     * </p>
     */
    private String url;
}
