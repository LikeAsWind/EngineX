package org.nstep.engine.module.message.enums;

import org.nstep.engine.framework.common.exception.ErrorCode;

/**
 * Message 错误码枚举类
 * <p>
 * system 系统，使用 1-003-000-000 段
 */
public interface ErrorCodeConstants {


    // ========== 消息模板信息 1-003-000-000 ==========
    ErrorCode TEMPLATE_NOT_EXISTS = new ErrorCode(1_003_000_000, "消息模板信息不存在!");
    ErrorCode TEMPLATE_INFO_FETCH_FAILED = new ErrorCode(1_003_000_001, "微信服务号模板信息获取失败!");
    ErrorCode EXCEEDED_DOMAIN_REDIRECT_LIMIT = new ErrorCode(1_003_000_002, "跳转链接不允许存在多个!");
    ErrorCode TEMPLATE_ID_NOT_EXIST = new ErrorCode(1_003_000_003, "微信模板库没有该模板!");
    ErrorCode PLACEHOLDER_RESOLUTION_FAILURE = new ErrorCode(1_003_000_004, "占位符数据解析失败!");
    ErrorCode INVALID_FIELDS = new ErrorCode(1_003_000_004, "存在未通过校验字段!");

    // ========== 消息模板信息 1-003-001-000 ==========
    ErrorCode ACCOUNT_NOT_EXISTS = new ErrorCode(1_003_000_000, "渠道配置信息不存在");
    ErrorCode CHANNEL_CODE_EMPTY = new ErrorCode(1_003_000_000, "发送账号为空");


}
