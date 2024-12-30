package org.nstep.engine.module.message.controller.admin.content.dto;

import lombok.*;


/**
 * 微信服务号消息模型
 **/
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatServiceAccountContentModel extends ContentModel {

    /**
     * 微信消息模板id
     */
    private String templateId;

    /**
     * 点击跳转链接类型 10.外部链接 20.小程序
     */
    private Integer linkType;

    /**
     * 外部http链接
     */
    private String url;

    /**
     * 模板消息跳转小程序的appid
     */
    private String miniProgramId;

    /**
     * 模板消息跳转小程序的页面路径
     */
    private String path;

    /**
     * 模板占位符信息 key:value
     */
    private String content;

}
