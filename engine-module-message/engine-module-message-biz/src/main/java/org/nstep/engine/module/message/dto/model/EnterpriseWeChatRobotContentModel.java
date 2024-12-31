package org.nstep.engine.module.message.dto.model;

import lombok.*;

/**
 * 企业微信机器人消息体
 * 该类用于表示企业微信机器人发送的消息内容
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseWeChatRobotContentModel extends ContentModel {

    /**
     * 发送消息类型
     * 表示发送的消息类型，如文本、markdown、图片等
     */
    private String sendType;

    /**
     * 企业微信机器人消息支持类型：
     * 【文本消息】内容
     * 【markdown消息】内容
     * 【图片消息】内容
     * 【图文消息】内容
     * 【文件消息】内容
     * 该字段存储消息的实际内容，根据消息类型不同，内容形式也不同
     */
    private String content;
}
