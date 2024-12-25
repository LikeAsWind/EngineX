package org.nstep.engine.module.system.api.mail.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 邮件发送给 Admin 或者 Member 用户的请求数据传输对象
 * <p>
 * 该 DTO 用于通过 RPC 服务发送邮件给指定用户，支持指定邮件模板和参数。
 */
@Schema(description = "RPC 服务 - 邮件发送给 Admin 或者 Member 用户 Request DTO")
@Data
public class MailSendSingleToUserReqDTO {

    /**
     * 用户编号
     * <p>
     * 用于标识目标用户的唯一编号。
     */
    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    /**
     * 邮箱地址
     * <p>
     * 目标用户的邮箱地址，必须是有效的邮箱格式。
     */
    @Schema(description = "手机号", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    @Email
    private String mail;

    /**
     * 邮件模板编号
     * <p>
     * 指定用于发送的邮件模板编号。
     * 不能为空，必须提供有效的模板编号。
     */
    @Schema(description = "邮件模板编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "USER_SEND")
    @NotNull(message = "邮件模板编号不能为空")
    private String templateCode;

    /**
     * 邮件模板参数
     * <p>
     * 用于替换邮件模板中的占位符的参数，键值对形式。
     */
    @Schema(description = "邮件模板参数")
    private Map<String, Object> templateParams;

}
