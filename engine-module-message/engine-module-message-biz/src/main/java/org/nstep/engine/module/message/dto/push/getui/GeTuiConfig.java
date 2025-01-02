package org.nstep.engine.module.message.dto.push.getui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户个推账号配置
 * 该类用于封装个推（GeTui）账号的配置信息，包括应用的ID、密钥和主密钥。
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class GeTuiConfig {

    /**
     * 应用ID
     * 用于标识个推平台上的应用，每个应用在个推平台上都有唯一的ID。
     */
    private String appId;

    /**
     * 应用密钥
     * 用于标识应用的唯一密钥，用于个推平台的身份验证。
     */
    private String appKey;

    /**
     * 主密钥
     * 用于个推平台的高级权限操作，通常用于推送任务的认证。
     */
    private String masterSecret;
}
