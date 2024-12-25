package org.nstep.engine.module.system.api.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.nstep.engine.framework.common.enums.UserTypeEnum;
import org.nstep.engine.framework.common.validation.InEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 微信小程序订阅消息发送请求 DTO
 * <p>
 * 该类用于接收发送微信小程序订阅消息的请求，包括用户编号、用户类型、消息模板标题、跳转页面以及模板内容的参数。
 */
@Schema(description = "RPC 服务 - 微信小程序订阅消息发送 Request DTO")
@Data
public class SocialWxaSubscribeMessageSendReqDTO {

    /**
     * 用户编号
     * <p>
     * 用于标识接收订阅消息的用户。
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "用户编号不能为空")
    private Long userId;

    /**
     * 用户类型
     * <p>
     * 用于标识用户类型，参见 `UserTypeEnum` 枚举。
     */
    @Schema(description = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @InEnum(UserTypeEnum.class)
    @NotNull(message = "用户类型不能为空")
    private Integer userType;

    /**
     * 消息模板标题
     * <p>
     * 用于标识微信小程序的消息模板的标题。
     */
    @Schema(description = "消息模版标题", requiredMode = Schema.RequiredMode.REQUIRED, example = "模版标题")
    @NotEmpty(message = "消息模版标题不能为空")
    private String templateTitle;

    /**
     * 点击模板卡片后的跳转页面
     * <p>
     * 该字段支持带参数的页面跳转（示例：`index?foo=bar`）。如果不填，则模板无跳转。
     */
    @Schema(description = "点击模板卡片后的跳转页面，仅限本小程序内的页面", example = "pages/index?foo=bar")
    private String page;

    /**
     * 模板内容的参数
     * <p>
     * 存储模板消息的具体内容，通过键值对形式提供。
     */
    @Schema(description = "模板内容的参数")
    private Map<String, String> messages;

    /**
     * 向模板消息中添加一个参数
     * <p>
     * 该方法用于动态添加模板消息的参数。每个参数以键值对形式传递。
     *
     * @param key   参数名
     * @param value 参数值
     * @return 当前对象实例
     */
    public SocialWxaSubscribeMessageSendReqDTO addMessage(String key, String value) {
        if (messages == null) {
            messages = new HashMap<>();
        }
        messages.put(key, value);
        return this;
    }
}
