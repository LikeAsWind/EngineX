package org.nstep.engine.module.message.rabbitmq.consumer;

import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.domain.SendTaskInfo;
import org.nstep.engine.module.message.domain.content.SendContent;
import org.nstep.engine.module.message.domain.weChat.Task;
import org.nstep.engine.module.message.util.DataUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 消费 MQ 的消息实现类
 * <p>
 * 该类实现了 `ConsumerService` 接口，负责消费来自消息队列的消息，并使用线程池异步执行发送任务。
 * 它处理从 MQ 中获取到的消息内容，并通过相应的线程池执行任务。
 */
@Service
@Slf4j
public class ConsumerImpl implements ConsumerService {

    @Resource
    private ApplicationContext applicationContext; // Spring 应用上下文，用于获取任务实例

    @Resource
    private Map<Integer, ThreadPoolExecutor> dtpThreadPoolExecutors; // 渠道线程池映射，根据不同的发送渠道选择相应的线程池

    @Resource
    private DataUtil dataUtil; // 工具类，处理发送数据和统计信息

    /**
     * 使用线程池消费发送任务集合中的每一个任务信息
     * <p>
     * 该方法根据消息内容中的发送任务集合，将每个发送任务交给相应的线程池执行。每个发送任务根据发送渠道分配到对应的线程池中执行。
     * 如果在执行过程中发生异常，会记录错误信息并调用 `confirmSend` 方法处理异常。
     *
     * @param sendContext 发送任务的上下文，包含多个发送任务信息和相关元数据
     */
    @Override
    public void consumerSend(SendContent sendContext) {
        Integer sendChannel = sendContext.getSendChannel(); // 获取发送渠道
        List<SendTaskInfo> sendTasks = sendContext.getSendTasks(); // 获取当前的所有发送任务

        // 遍历每个发送任务并提交给线程池执行
        for (SendTaskInfo sendTaskInfo : sendTasks) {
            try {
                // 从 Spring 上下文获取 Task 实例，并设置相应的发送任务信息
                Task task = applicationContext.getBean(Task.class).setSendTaskInfo(sendTaskInfo);
                // 根据发送渠道获取对应的线程池，执行任务
                dtpThreadPoolExecutors.get(sendChannel).execute(task);
            } catch (Exception e) {
                // 处理消息消费失败的异常
                log.error("消息消费失败:{}", Throwables.getStackTraceAsString(e));
                // 调用 DataUtil 的 confirmSend 方法，确认失败任务的状态
                dataUtil.confirmSend(null, sendTaskInfo.getMessageId(), sendTaskInfo.getSendMessageKey(), sendTaskInfo.getSendTaskId(), e);
            }
        }
    }

    /**
     * 撤回消息的处理方法
     * <p>
     * 当前方法尚未实现，未来可能需要用于撤回已发送的消息。
     */
    @Override
    public void consumerRecall() {
        // 该方法为空，等待实现
    }
}
