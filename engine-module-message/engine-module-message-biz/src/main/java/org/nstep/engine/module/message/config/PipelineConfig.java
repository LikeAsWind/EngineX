package org.nstep.engine.module.message.config;


import jakarta.annotation.Resource;
import org.nstep.engine.module.message.process.management.ProcessTemplate;
import org.nstep.engine.module.message.process.management.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.Collections;

/**
 * 业务流水线配置类，用于配置消息发送和定时任务的责任链。
 * 该类通过Spring的@Configuration注解配置不同的处理步骤（责任链中的各个环节），
 * 并通过@Bean注解定义了消息发送和定时任务的责任链模板。
 */
@Configuration // 将该类标记为Spring的配置类
public class PipelineConfig {

    @Resource
    public PermissionVerificationProcess permissionVerificationProcess; // 注入权限验证处理步骤

    @Resource
    private PreCheckProcess preCheckProcess; // 注入预检查处理步骤

    @Resource
    private VariableClassificationProcess variableClassificationProcess; // 注入变量分类处理步骤

    @Resource
    private ReceiverCheckProcess receiverCheckProcess; // 注入接收者检查处理步骤

    @Resource
    private DataPlaceholderProcess dataPlaceholderProcess; // 注入数据占位符处理步骤

    @Resource
    private TypeMappingProcess typeMappingProcess; // 注入类型映射处理步骤

    @Resource
    private SendMqProcess sendMqProcess; // 注入发送到MQ的处理步骤

    @Resource
    private CronTaskDataProcess cronTaskDataProcess; // 注入定时任务数据处理步骤


    /**
     * 消息发送责任链，构建并返回完整的消息发送处理链模板。
     * 该责任链依次包含多个处理步骤，从权限验证到发送消息到MQ。
     *
     * @return 返回构建好的消息发送责任链模板
     */
    @Bean("sendMessageTemplate") // 通过@Bean注解将该方法的返回值注册为Spring容器中的一个bean
    public ProcessTemplate sendMessageTemplate() {
        // 使用ProcessTemplate.builder()创建一个构建器，构建责任链
        return ProcessTemplate.builder()
                // 将各个处理步骤按照顺序加入责任链
                .processes(Arrays.asList(
                        // 权限验证处理步骤
                        permissionVerificationProcess,
                        // 预检查处理步骤
                        preCheckProcess,
                        // 变量分类处理步骤
                        variableClassificationProcess,
                        // 接收者检查处理步骤
                        receiverCheckProcess,
                        // 数据占位符处理步骤
                        dataPlaceholderProcess,
                        // 类型映射处理步骤
                        typeMappingProcess,
                        // 发送消息到MQ的处理步骤
                        sendMqProcess
                )).build(); // 完成责任链的构建，并返回构建好的ProcessTemplate实例
    }

    /**
     * 定时任务处理责任链，构建并返回定时任务处理的责任链模板。
     * 该责任链只有一个处理步骤，即处理定时任务的数据。
     *
     * @return 返回构建好的定时任务处理责任链模板
     */
    @Bean("cronTaskTemplate") // 通过@Bean注解将该方法的返回值注册为Spring容器中的一个bean
    public ProcessTemplate cronTaskTemplate() {
        // 使用ProcessTemplate.builder()创建一个构建器，构建责任链
        return ProcessTemplate.builder()
                // 将定时任务处理步骤加入责任链
                .processes(Collections.singletonList(
                        // 定时任务数据处理步骤
                        cronTaskDataProcess
                )).build(); // 完成责任链的构建，并返回构建好的ProcessTemplate实例
    }

}
