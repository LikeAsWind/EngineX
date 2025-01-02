package org.nstep.engine.module.message.task;

import cn.hutool.core.util.StrUtil;
import com.google.common.base.Throwables;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.constant.QueueConstants;
import org.nstep.engine.module.message.handler.cron.CronTaskHandler;
import org.nstep.engine.module.message.util.DataUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.*;


/**
 * 定时任务入口
 * 该类是定时任务的入口，负责监听并执行定时任务。它使用了线程池和阻塞队列来处理任务。
 * 任务的执行通过XXL-JOB调度，任务处理逻辑交给 `CronTaskHandler` 来完成。
 */
@Component
@Slf4j
public class CronTask {

    @Resource
    private ThreadPoolExecutor xxlDtpExecutor;  // 用于执行定时任务的线程池

    @Resource
    private CronTaskHandler cronTaskHandler;  // 处理定时任务的业务逻辑

    @Resource
    private DataUtil dataUtil;  // 用于记录定时任务的状态

    // 监听线程池，用于监听任务队列并执行任务
    private static final ExecutorService LISTEN_EXECUTOR = Executors.newCachedThreadPool();

    // 创建一个阻塞队列，用来存放待处理的定时任务
    private final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>(QueueConstants.BIG_QUEUE_SIZE);

    /**
     * 初始化任务队列
     * 该方法在类加载时被调用，用于启动一个监听线程，监听任务队列中的任务，并将其提交到线程池执行。
     */
    @PostConstruct
    public void initQueue() {
        LISTEN_EXECUTOR.execute(() -> {
            while (true) {
                try {
                    // 从任务队列中取出任务并提交给线程池执行
                    xxlDtpExecutor.execute(taskQueue.take());
                } catch (Exception e) {
                    logError("定时任务处理异常", e);
                }
            }
        });
    }

    /**
     * 定时任务执行入口
     * 该方法由XXL-JOB定时任务调度框架触发，用于执行定时任务。
     * 它解析定时任务的参数，并将任务提交到任务队列中。
     */
    @XxlJob("messageJob")
    public void cronTaskExecutor() {
        // 获取XXL-JOB传入的任务参数
        String jobParam = XxlJobHelper.getJobParam();
        List<String> params = StrUtil.split(jobParam, MessageDataConstants.SEPARATOR);

        // 记录定时任务的启动状态
        dataUtil.recordCronTaskStatus(MessageDataConstants.CRON_TASK_SCHEDULING, Long.valueOf(params.get(0)), Long.valueOf(params.get(1)), "消息任务进入启动阶段，正在校验消息任务...");

        try {
            // 将任务提交到任务队列
            taskQueue.put(() -> cronTaskHandler.Handler(Long.valueOf(params.get(0)), Long.valueOf(params.get(1))));
        } catch (Exception e) {
            logError("定时任务提交异常", e);
        }
    }

    /**
     * 统一的异常日志记录方法
     *
     * @param message 错误信息
     * @param e       异常对象
     */
    private void logError(String message, Exception e) {
        log.error("{}:{}", message, Throwables.getStackTraceAsString(e));
    }
}
