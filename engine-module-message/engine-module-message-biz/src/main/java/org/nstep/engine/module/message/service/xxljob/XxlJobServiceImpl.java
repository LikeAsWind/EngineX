package org.nstep.engine.module.message.service.xxljob;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.log.Log;
import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.enums.ExecutorBlockStrategyEnum;
import com.xxl.job.core.glue.GlueTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.helper.DataUtil;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.framework.xxljob.domain.CronTaskCords;
import org.nstep.engine.framework.xxljob.domain.XxlJobInfo;
import org.nstep.engine.framework.xxljob.enums.ExecutorRouteStrategyEnum;
import org.nstep.engine.framework.xxljob.enums.MisfireStrategyEnum;
import org.nstep.engine.framework.xxljob.enums.ScheduleTypeEnum;
import org.nstep.engine.framework.xxljob.util.XxlJobUtil;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dal.mysql.template.TemplateMapper;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;

/**
 * XxlJobServiceImpl 是一个实现类，用于管理和操作XXL Job定时任务的相关逻辑。
 * 它提供了保存、启动、停止、更新和删除定时任务的方法，利用XXL Job的API与调度中心进行交互。
 */
@Service
@Validated
@Slf4j
public class XxlJobServiceImpl implements XxlJobService {

    // 注入相关依赖
    @Resource
    private XxlJobUtil xxlJobUtil;  // 用于与XXL Job调度中心交互
    @Resource
    private TemplateMapper templateMapper;  // 用于操作模板数据
    @Resource
    private DataUtil dataUtil;  // 数据处理工具类
    @Resource
    private StringRedisTemplate stringRedisTemplate;  // 用于操作Redis
    @Value("${xxl.job.executor.jobHandlerName}")
    private String jobName;  // 任务处理器名称

    /**
     * 保存定时任务模板信息
     *
     * @param messageTemplate 任务模板对象
     * @return 任务ID
     */
    @Override
    public Integer save(TemplateDO messageTemplate) {
        // 构建XXL Job任务信息
        XxlJobInfo xxlJobInfo = buildXxlJobInfo(messageTemplate);
        // 调用XXL Job的API添加任务
        String result = xxlJobUtil.add(xxlJobInfo);
        ReturnT<?> returnT = JSON.parseObject(result, ReturnT.class);
        // 如果任务添加失败，抛出异常
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw exception(ErrorCodeConstants.CRON_EXPRESSION_ERROR, messageTemplate.getName(), "保存", returnT.getMsg());
        }
        // 返回任务ID
        return Integer.valueOf((String) returnT.getContent());
    }

    /**
     * 根据任务模板构建XXL Job任务信息
     *
     * @param messageTemplate 任务模板对象
     * @return 构建的XXL Job任务信息
     */
    @Override
    public XxlJobInfo buildXxlJobInfo(TemplateDO messageTemplate) {
        String pushTime = messageTemplate.getExpectPushTime();
        String pushTimeTemp = pushTime;
        // 如果没有指定时间或时间为立即执行，设置为立即执行
        if (pushTime == null || MessageDataConstants.PUSH_NOW.equals(pushTime)) {
            pushTime = DateUtil.format(DateUtil.offsetSecond(new Date(), MessageDataConstants.DELAY_TIME), MessageDataConstants.CRON_FORMAT);
        }
        // 校验Cron表达式的合法性
        boolean validExpression = CronExpression.isValidExpression(pushTime);
        if (!validExpression && !MessageDataConstants.PUSH_NOW.equals(pushTimeTemp)) {
            throw exception(ErrorCodeConstants.CRON_EXPRESSION_INVALID_ERROR, messageTemplate.getName());
        }
        // 构建XXL Job任务信息
        XxlJobInfo xxlJobInfo = XxlJobInfo.builder()
                .jobGroup(xxlJobUtil.getGroupId())  // 获取任务组ID
                .jobDesc(messageTemplate.getName())  // 任务描述
                .author(messageTemplate.getCreator())  // 任务创建者
                .scheduleConf(pushTime)  // 调度配置（Cron表达式）
                .scheduleType(ScheduleTypeEnum.CRON.name())  // 调度类型（Cron）
                .misfireStrategy(MisfireStrategyEnum.DO_NOTHING.name())  // 调度失败策略（不做任何操作）
                .executorRouteStrategy(ExecutorRouteStrategyEnum.CONSISTENT_HASH.name())  // 执行器路由策略（一致性哈希）
                .executorHandler(jobName)  // 任务执行器
                .executorParam(messageTemplate.getId() + MessageDataConstants.SEPARATOR + SecurityFrameworkUtils.getLoginUserId())  // 任务参数（模板ID和发送者ID）
                .executorBlockStrategy(ExecutorBlockStrategyEnum.SERIAL_EXECUTION.name())  // 执行器阻塞策略（串行执行）
                .executorTimeout(MessageDataConstants.TIME_OUT)  // 执行超时
                .executorFailRetryCount(MessageDataConstants.RETRY_COUNT)  // 执行失败重试次数
                .glueType(GlueTypeEnum.BEAN.name())  // 执行器类型（Bean类型）
                .triggerStatus(MessageDataConstants.TRIGGER_STATUS_FALSE)  // 触发状态（未触发）
                .glueRemark(StrUtil.EMPTY)  // 执行器备注
                .glueSource(StrUtil.EMPTY)  // 执行器源码
                .alarmEmail(MessageDataConstants.NOTING_EMAIL)  // 告警邮箱
                .childJobId(StrUtil.EMPTY)  // 子任务ID
                .build();

        // 如果模板有Cron任务ID，设置任务ID
        if (Objects.nonNull(messageTemplate.getCronTaskId())) {
            xxlJobInfo.setId(Math.toIntExact(messageTemplate.getCronTaskId()));
        }
        return xxlJobInfo;
    }

    /**
     * 启动定时任务
     *
     * @param id 任务ID
     */
    @Override
    public void start(Long id) {
        Long sender = SecurityFrameworkUtils.getLoginUserId();  // 获取当前登录用户ID
        TemplateDO messageTemplate = templateMapper.selectById(id);  // 查询任务模板
        if (!Objects.nonNull(messageTemplate.getCronTaskId())) {
            // 如果Cron任务ID不存在，先保存任务
            Integer cronTaskId = save(messageTemplate);
            messageTemplate.setCronTaskId(Long.valueOf(cronTaskId));
        } else {
            // 如果Cron任务ID存在，更新任务
            update(messageTemplate);
        }
        // 启动任务
        ReturnT<?> returnT = JSON.parseObject(xxlJobUtil.start(Math.toIntExact(messageTemplate.getCronTaskId())), ReturnT.class);
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw exception(ErrorCodeConstants.CRON_EXPRESSION_ERROR, messageTemplate.getName(), "启动", returnT.getMsg());
        }
        // 更新任务状态为已启动
        messageTemplate.setMsgStatus(MessageDataConstants.MSG_START);
        messageTemplate.setCurrentId(sender);  // 锁定定时模板资源
        templateMapper.updateById(messageTemplate);  // 更新数据库中的任务状态
        // 记录定时任务状态到Redis
        CronTaskCords taskCords = CronTaskCords.builder()
                .expectPushTime(messageTemplate.getExpectPushTime())
                .status(MessageDataConstants.CRON_TASK_STARTING)
                .startTime(LocalDateTime.now())
                .log("消息任务已开始，正在等待调度...")
                .messageTemplateId(id)
                .sender(sender)
                .sendChannel(null)  // 发送渠道
                .build();
        stringRedisTemplate.opsForValue().set(MessageDataConstants.CRON_TASK_STATUS_KEY + sender + ":" + id, JSON.toJSONString(taskCords));
    }

    /**
     * 停止定时任务
     *
     * @param id 任务ID
     */
    @Override
    public void stop(Long id) {
        TemplateDO messageTemplate = templateMapper.selectById(id);  // 查询任务模板
        // 停止任务
        ReturnT<?> returnT = JSON.parseObject(xxlJobUtil.stop(Math.toIntExact(messageTemplate.getCronTaskId())), ReturnT.class);
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw exception(ErrorCodeConstants.CRON_EXPRESSION_ERROR, messageTemplate.getName(), "暂停", returnT.getMsg());
        }
        // 更新任务状态为已停止
        messageTemplate.setMsgStatus(MessageDataConstants.MSG_STOP);
        messageTemplate.setCurrentId(-1L);  // 释放定时模板权限
        templateMapper.updateById(messageTemplate);  // 更新数据库中的任务状态
    }

    /**
     * 更新定时任务
     *
     * @param messageTemplate 任务模板对象
     */
    @Override
    public void update(TemplateDO messageTemplate) {
        // 构建XXL Job任务信息
        XxlJobInfo xxlJobInfo = buildXxlJobInfo(messageTemplate);
        // 更新任务
        ReturnT<?> returnT = JSON.parseObject(xxlJobUtil.update(xxlJobInfo), ReturnT.class);
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw exception(ErrorCodeConstants.CRON_EXPRESSION_ERROR, messageTemplate.getName(), "修改", returnT.getMsg());
        }
    }

    /**
     * 删除定时任务
     *
     * @param id 任务ID
     */
    @Override
    public void remove(Long id) {
        // 删除任务
        ReturnT<?> returnT = JSON.parseObject(xxlJobUtil.remove(Math.toIntExact(id)), ReturnT.class);
        if (ReturnT.SUCCESS_CODE != returnT.getCode()) {
            throw exception(ErrorCodeConstants.SCHEDULED_TASK_DELETION_FAILURE, returnT.getMsg());
        }
    }
}
