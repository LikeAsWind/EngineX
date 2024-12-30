package org.nstep.engine.module.message.controller.admin.template;

/**
 * @author yangzhitong
 * @desc
 * @date 2024/12/30 15:55
 **/

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateSendReqVO;
import org.nstep.engine.module.message.service.template.MessageManagementService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 消息模板信息控制器
 * 该控制器处理所有与消息模板相关的请求，包括创建、更新、删除、查询、分页查询、导出等操作。
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
     * 该方法接收一个 SendForm 对象作为请求体，该对象包含了要发送的消息的详细信息。
     * 首先，方法会调用 sendMessageService 的 send 方法来发送这个消息。
     * 如果发送成功，方法会返回一个包含成功信息的 CommonResult 对象。
     * 如果发送失败，方法会返回一个包含错误信息的 CommonResult 对象。
     *
     * @param sendForm 要发送的消息对象
     * @return 包含操作结果的 CommonResult 对象
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息--实时")
    @PreAuthorize("@ss.hasPermission('message:management:send')")
    public CommonResult<?> send(@Valid @RequestBody TemplateSendReqVO sendForm) {
        return messageManagementService.send(sendForm);
    }

    @GetMapping("/start/{id}")
    @Operation(summary = "启动消息-定时任务")
    @PreAuthorize("@ss.hasPermission('message:management:start')") // 权限控制，确保用户有权限
    public CommonResult<?> start(@PathVariable Long id) {
        messageManagementService.start(id);
        return success(true);
    }

    @GetMapping("/stop/{id}")
    @Operation(summary = "暂停消息-定时任务")
    @PreAuthorize("@ss.hasPermission('message:management:stop')") // 权限控制，确保用户有权限
    public CommonResult<?> stop(@PathVariable Long id) {
        messageManagementService.stop(id);
        return success(true);
    }

}
