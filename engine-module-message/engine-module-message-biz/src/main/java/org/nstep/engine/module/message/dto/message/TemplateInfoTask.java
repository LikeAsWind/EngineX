package org.nstep.engine.module.message.dto.message;


import jakarta.annotation.Resource;
import lombok.Data;
import lombok.experimental.Accessors;
import org.nstep.engine.module.message.handler.real.ChannelHandler;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 发送任务
 * <p>
 * 该类实现了 Runnable 接口，用于表示一个发送任务。它包含一个 TemplateSendTask 对象和一个 ChannelHandler 映射，用于根据消息模板的发送渠道来处理任务。
 * 该任务会在独立线程中运行，并根据具体的发送渠道通过不同的 ChannelHandler 进行处理。
 */
@Data
@Accessors(chain = true) // 链式setter并返回对象
@Component // 将该类标记为Spring管理的Bean
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // 指定Bean的作用域为原型作用域，即每次请求都会创建一个新的实例
public class TemplateInfoTask implements Runnable {

    /**
     * 发送任务信息
     * <p>
     * 该字段保存了与该任务相关的所有任务信息，包括消息模板、发送的内容和其他元数据。
     */
    private TemplateSendTask TemplateSendTask;

    /**
     * 渠道处理器映射
     * <p>
     * 该映射存储了不同渠道类型（如短信、邮件等）对应的处理器，用于处理发送任务。
     * 每个渠道都有一个对应的 ChannelHandler，用来处理消息的发送逻辑。
     */
    @Resource // 注入渠道处理器映射
    private Map<Integer, ChannelHandler> channelHandlers;

    /**
     * 任务执行方法
     * <p>
     * 该方法实现了 Runnable 接口的 run 方法，用于执行发送任务。它根据消息模板的发送渠道，从 channelHandlers 映射中获取对应的处理器，
     * 并调用其 handler 方法来处理发送任务信息。
     */
    @Override
    public void run() {
        // 路由到对应的渠道handler，获取该渠道对应的处理器并执行处理逻辑
        channelHandlers.get(TemplateSendTask.getMessageTemplate().getSendChannel()).handler(TemplateSendTask);
    }
}
