package org.nstep.engine.module.message.dto.dingding;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 钉钉群自定义机器人账号配置信息类，封装了钉钉自定义机器人所需的配置信息。
 * 该类用于存储钉钉自定义群机器人所需的密钥和Webhook地址，用于向钉钉群发送消息。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingRobotConfig {

    /**
     * 钉钉群自定义机器人的密钥，用于验证请求的合法性。
     * 该密钥在钉钉群机器人配置中获取，用于防止非法请求。
     */
    private String secret;

    /**
     * 钉钉群自定义机器人的Webhook地址，用于发送消息到指定的钉钉群。
     * Webhook地址是钉钉为每个自定义机器人分配的唯一URL，发送请求时需要使用该URL。
     */
    private String webhook;
}
