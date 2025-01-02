package org.nstep.engine.module.message.dto.weChat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业微信机器人账号配置
 * 该类用于封装企业微信机器人账号的配置信息，主要包括群机器人的 webhook 地址。
 */
@Data 
@Builder 
@AllArgsConstructor 
@NoArgsConstructor 
public class EnterpriseWeChatRobotConfig {

    /**
     * 群机器人的 webhook
     * 企业微信机器人发送消息需要使用的 webhook 地址。
     * 必选配置项。
     */
    private String webhook;
}
