package org.nstep.engine.module.message.dto.content;

import lombok.*;


/**
 * 微信服务号消息模型
 * <p>
 * 该类用于表示微信服务号发送的消息模型。它包含了微信消息模板的相关信息以及消息跳转链接、占位符等内容。
 * 通过该模型可以构建微信服务号消息模板的内容，支持外部链接和小程序跳转。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatServiceAccountContentModel extends ContentModel {

    /**
     * 微信消息模板id
     * <p>
     * 该字段用于指定使用的微信模板的ID。每个模板都有唯一的ID，可以通过模板ID来识别并使用特定的模板。
     */
    private String templateId;

    /**
     * 点击跳转链接类型
     * <p>
     * 该字段指定消息点击后跳转的链接类型：
     * 10 - 外部链接
     * 20 - 小程序
     */
    private Integer linkType;

    /**
     * 外部http链接
     * <p>
     * 该字段用于指定点击消息后跳转的外部HTTP链接。如果linkType为10，则该字段用于存储跳转的外部链接。
     */
    private String url;

    /**
     * 模板消息跳转小程序的appid
     * <p>
     * 该字段用于指定模板消息跳转的小程序的AppID。如果linkType为20，则该字段用于存储小程序的AppID。
     */
    private String miniProgramId;

    /**
     * 模板消息跳转小程序的页面路径
     * <p>
     * 该字段用于指定模板消息跳转到的小程序的页面路径。如果linkType为20，则该字段用于存储小程序的页面路径。
     */
    private String path;

    /**
     * 模板占位符信息
     * <p>
     * 该字段用于存储模板消息的占位符信息，格式为key:value。占位符会被替换为实际内容，用于个性化消息。
     */
    private String content;
}
