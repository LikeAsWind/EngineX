package org.nstep.engine.module.message.enums;

import org.nstep.engine.framework.common.exception.ErrorCode;

/**
 * Message 错误码枚举类
 * <p>
 * system 系统，使用 1-003-000-000 段
 */
public interface ErrorCodeConstants {


    // ========== 消息模板信息 1-003-000-000 ==========
    ErrorCode TEMPLATE_NOT_EXISTS = new ErrorCode(1_003_000_001, "消息模板信息不存在");

}
