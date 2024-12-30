package org.nstep.engine.module.message.domain.weChat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 微信公众号账号配置信息
 * <p>
 * 该类用于封装微信公众号的配置信息，包括 appId、secret 和 token。通常用于与微信 API 的交互，进行身份验证和授权。
 * </p>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeChatServiceAccountConfig {

    /**
     * 公众号的唯一标识
     * <p>
     * 微信公众号的 appId 是一个由微信分配的唯一标识，用于标识公众号的身份。
     * </p>
     */
    private String appId;

    /**
     * 公众号的应用密钥
     * <p>
     * 微信公众号的 secret 是用于授权的密钥，通常与 appId 一起使用进行身份验证。
     * </p>
     */
    private String secret;

    /**
     * 公众号的消息加解密 Token
     * <p>
     * 微信公众号的 token 用于消息的加解密和验证消息来源。它是微信服务器和公众号之间通信的安全保障。
     * </p>
     */
    private String token;

}
