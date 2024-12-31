package org.nstep.engine.module.message.config;

import cn.hutool.core.util.StrUtil;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.constant.QueueConstants;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChannelConfig {

    // 缓存已支持的渠道 ID 列表
    public static final List<Integer> CHANNELS;

    // 缓存已支持的渠道名称列表，每个渠道的 handler 名称
    public static final List<String> CHANNEL_NAMES;

    // 缓存已支持的渠道中文名称列表，每个渠道的 handler 名称
    public static final List<String> CHANNEL_CN_NAMES;

    // 缓存需要跳过占位符替换的渠道列表，例如：短信和微信服务号
    public static final List<Integer> NEED_SKID;

    // 缓存各渠道的过期时间，渠道 ID -> 过期时间
    public static final Map<Integer, String> CHANNEL_EXP_TIME;

    // 静态代码块，用于初始化静态常量字段
    static {
        // 使用 StrUtil 将支持的渠道 ID 从常量中拆分并转换为整数列表
        CHANNELS = StrUtil.split(MessageDataConstants.SUPPORT_CHANNEL, MessageDataConstants.SEPARATOR)
                .stream().map(Integer::valueOf).collect(Collectors.toList());

        // 使用 StrUtil 将支持的渠道名称从常量中拆分为列表
        CHANNEL_NAMES = StrUtil.split(MessageDataConstants.SUPPORT_CHANNEL_NAME, MessageDataConstants.SEPARATOR);

        // 使用 StrUtil 将支持的渠道中文名称从常量中拆分为列表
        CHANNEL_CN_NAMES = StrUtil.split(MessageDataConstants.SUPPORT_CHANNEL_CN_NAME, MessageDataConstants.SEPARATOR);

        // 缓存需要跳过占位符替换的渠道（如短信和微信服务号）
        NEED_SKID = Arrays.asList(MessageDataConstants.SMS, MessageDataConstants.WECHAT_SERVICE_ACCOUNT);

        // 使用 Map.of 初始化各个渠道的过期时间
        CHANNEL_EXP_TIME = Map.of(
                MessageDataConstants.EMAIL, QueueConstants.EMAIL_EXPIRATION_TIME,  // 邮件渠道的过期时间
                MessageDataConstants.SMS, QueueConstants.SMS_EXPIRATION_TIME,  // 短信渠道的过期时间
                MessageDataConstants.DING_DING_ROBOT, QueueConstants.DING_DING_ROBOT_EXPIRATION_TIME,  // 钉钉机器人渠道的过期时间
                MessageDataConstants.WECHAT_SERVICE_ACCOUNT, QueueConstants.WECHAT_SERVICE_ACCOUNT_EXPIRATION_TIME,  // 微信服务号渠道的过期时间
                MessageDataConstants.PUSH, QueueConstants.PUSH_EXPIRATION_TIME,  // 推送渠道的过期时间
                MessageDataConstants.FEI_SHU_ROBOT, QueueConstants.FEI_SHU_ROBOT_EXPIRATION_TIME,  // 飞书机器人渠道的过期时间
                MessageDataConstants.ENTERPRISE_WECHAT_ROBOT, QueueConstants.ENTERPRISE_WECHAT_ROBOT_TIME  // 企业微信机器人渠道的过期时间
        );
    }
}
