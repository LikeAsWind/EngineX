package org.nstep.engine.module.message.dto.model;

import lombok.*;

/**
 * 邮箱信息模型，继承自ContentModel类，用于封装发送邮件所需的内容信息。
 * 该类存储邮件的标题、内容和可能的URL链接，适用于发送邮件时构建邮件内容。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmailContentModel extends ContentModel {

    /**
     * 邮件标题，用于设置邮件的主题。
     * 该字段表示邮件的主题内容。
     */
    private String title;

    /**
     * 邮件内容，表示邮件的正文部分。
     * 该字段包含邮件的主要内容，可以是纯文本或HTML格式。
     */
    private String content;

    /**
     * 邮件中包含的URL链接，通常用于邮件中嵌入的超链接。
     * 该字段可选，表示邮件正文中可能需要提供的一个URL。
     */
    private String url;
}
