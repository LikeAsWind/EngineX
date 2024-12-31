package org.nstep.engine.module.message.controller.admin.template;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.dto.message.TemplateSend;
import org.nstep.engine.module.message.service.template.MessageManagementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 消息模板信息控制器
 * <p>
 * 该控制器处理所有与消息模板相关的请求，包括发送消息、启动定时任务、暂停定时任务等操作。
 */
@Tag(name = "消息后台 - 消息模板信息")
@RestController
@RequestMapping("/message/management")
// 启用验证功能，确保请求参数符合要求
@Validated
public class MessageManagementController {

    @Resource
    private MessageManagementService messageManagementService;

    /**
     * 发送消息
     * <p>
     * 该方法接收一个 {@code TemplateSendReqVO} 对象作为请求体，该对象包含了要发送的消息的详细信息。
     * 调用 {@code messageManagementService} 的 {@code send} 方法执行消息发送操作。
     *
     * @param sendForm 包含消息发送所需信息的请求对象
     * @return 包含操作结果的 {@code CommonResult} 对象，表示发送操作的结果
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息--实时")
    @PreAuthorize("@ss.hasPermission('message:management:send')")
    public CommonResult<?> send(@Valid @RequestBody TemplateSend sendForm) {
        return messageManagementService.send(sendForm);
    }

    /**
     * 启动消息 - 定时任务
     * <p>
     * 根据消息模板的 ID，启动相应的定时任务。
     * 调用 {@code messageManagementService} 的 {@code start} 方法执行任务启动操作。
     *
     * @param id 消息模板的唯一标识 ID
     * @return 包含操作结果的 {@code CommonResult} 对象，表示启动操作的结果
     */
    @GetMapping("/start/{id}")
    @Operation(summary = "启动消息-定时任务")
    @PreAuthorize("@ss.hasPermission('message:management:start')") // 权限控制，确保用户有权限
    public CommonResult<?> start(@PathVariable Long id) {
        messageManagementService.start(id);
        return success(true);
    }

    /**
     * 暂停消息 - 定时任务
     * <p>
     * 根据消息模板的 ID，暂停相应的定时任务。
     * 调用 {@code messageManagementService} 的 {@code stop} 方法执行任务暂停操作。
     *
     * @param id 消息模板的唯一标识 ID
     * @return 包含操作结果的 {@code CommonResult} 对象，表示暂停操作的结果
     */
    @GetMapping("/stop/{id}")
    @Operation(summary = "暂停消息-定时任务")
    @PreAuthorize("@ss.hasPermission('message:management:stop')") // 权限控制，确保用户有权限
    public CommonResult<?> stop(@PathVariable Long id) {
        messageManagementService.stop(id);
        return success(true);
    }
}
