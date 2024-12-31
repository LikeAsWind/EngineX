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
    ErrorCode INVALID_FIELDS = new ErrorCode(1_003_000_005, "存在未通过校验字段!");
    ErrorCode CRON_EXPRESSION_ERROR = new ErrorCode(1_003_000_006, "定时消息任务:{} {}失败:{}!");
    ErrorCode CRON_EXPRESSION_INVALID_ERROR = new ErrorCode(1_003_000_007, "定时消息任务: {} cron表达式存在错误 请修改!");
    ErrorCode SCHEDULED_TASK_DELETION_FAILURE = new ErrorCode(1_003_000_008, "定时消息任务删除失败：{}!");
    ErrorCode TEMPLATE_ALREADY_USED = new ErrorCode(1_003_000_009, "该模板已被其它用户使用!");
    ErrorCode TEMPLATE_NOT_APPROVED = new ErrorCode(1_003_000_010, "该模板尚未通过审核!");
    ErrorCode PROCESS_CONTEXT_INTERRUPTION = new ErrorCode(1_003_000_011, "发送流程上下文断裂!");
    ErrorCode MESSAGE_TEMPLATE_ID_RECEIVER_EMPTY = new ErrorCode(1_003_000_012, "消息模板id: {} 接受者为空!");
    ErrorCode TEMPLATE_ID_IS_NULL = new ErrorCode(1_003_000_013, "模板id为空!");
    ErrorCode MESSAGE_TEMPLATE_ID_PLACEHOLDER_NEEDS_VALUE = new ErrorCode(1_003_000_014, "消息模板id:{} 消息模板带有占位符 请赋值!");
    ErrorCode MESSAGE_TEMPLATE_ID_PLACEHOLDER_DATA_EMPTY = new ErrorCode(1_003_000_015, "消息模板id:{} 存在占位符数据为空!");
    ErrorCode NO_NEED_TO_ASSIGN_PLACEHOLDER = new ErrorCode(1_003_000_016, "无需要赋值的占位符! ");
    ErrorCode RECEIVER_AND_PLACEHOLDER_DATA_COUNT_MISMATCH = new ErrorCode(1_003_000_017, "接受者和占位符数据数量不一致! ");
    ErrorCode ILLEGAL_RECIPIENT = new ErrorCode(1_003_000_018, "存在非法接受者:{} ");
    ErrorCode MQ_SEND_EXCEPTION = new ErrorCode(1_003_000_019, "消息发送mq异常:{} ");
    ErrorCode ILLEGAL_OPERATION_USER = new ErrorCode(1_003_000_020, "非法操作用户");
    ErrorCode IS_NULL_TEMPLATE = new ErrorCode(1_003_000_021, "{} is null");
    ErrorCode MESSAGE_DATA_LOST = new ErrorCode(1_003_000_022, "消息数据丢失");
    ErrorCode CONTEXT_BROKEN = new ErrorCode(1_003_000_023, "定时任务处理流程上下文断裂");
    ErrorCode READ_CSV_FAILED = new ErrorCode(1_003_000_024, "读取CSV文件失败: {} ");
    ErrorCode MESSAGE_CROWD_FILE_EMPTY = new ErrorCode(1_003_000_025, "消息:{} 人群文件为空  ");

    // ========== 消息模板信息 1-003-001-000 ==========
    ErrorCode ACCOUNT_NOT_EXISTS = new ErrorCode(1_003_000_000, "渠道配置信息不存在");
    ErrorCode CHANNEL_CODE_EMPTY = new ErrorCode(1_003_000_000, "发送账号为空");


}
