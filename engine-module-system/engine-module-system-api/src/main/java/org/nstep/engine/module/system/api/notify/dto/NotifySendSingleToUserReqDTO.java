package org.nstep.engine.module.system.api.notify.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 站内信发送给 Admin 或者 Member 用户的请求数据传输对象
 * <p>
 * 该 DTO 用于通过 RPC 服务发送站内信给指定用户，支持指定站内信模板和参数。
 */
@Schema(description = "RPC 服务 - 站内信发送给 Admin 或者 Member 用户 Request DTO")
@Data
public class NotifySendSingleToUserReqDTO {

    /**
     * 用户编号
     * <p>
     * 用于标识目标用户的唯一编号。
     * 必须提供有效的用户编号。
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    /**
     * 站内信模板编号
     * <p>
     * 指定用于发送的站内信模板编号。
     * 必须提供有效的模板编号，不能为空。
     */
    @Schema(description = "站内信模板编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "USER_SEND")
    @NotEmpty(message = "站内信模板编号不能为空")
    private String templateCode;

    /**
     * 站内信模板参数
     * <p>
     * 用于替换站内信模板中的占位符的参数，键值对形式。
     * 该字段可选，若模板中有占位符需要替换时，应提供相应的参数。
     */
    @Schema(description = "邮件模板参数")
    private Map<String, Object> templateParams;
}
