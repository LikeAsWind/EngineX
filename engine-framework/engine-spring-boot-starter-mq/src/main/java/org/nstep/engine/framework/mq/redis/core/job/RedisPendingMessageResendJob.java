package org.nstep.engine.framework.mq.redis.core.job;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.mq.redis.core.RedisMQTemplate;
import org.nstep.engine.framework.mq.redis.core.stream.AbstractRedisStreamMessageListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 这个任务用于处理，crash 之后的消费者未消费完的消息
 */
@Slf4j
@AllArgsConstructor
public class RedisPendingMessageResendJob {

    // Redis 锁的 key，用于防止并发执行任务
    private static final String LOCK_KEY = "redis:pending:msg:lock";

    /**
     * 消息超时时间，默认 5 分钟
     * <p>
     * 1. 超时的消息才会被重新投递
     * 2. 由于定时任务 1 分钟一次，消息超时后不会被立即重投，极端情况下消息5分钟过期后，再等 1 分钟才会被扫瞄到
     */
    private static final int EXPIRE_TIME = 5 * 60;

    private final List<AbstractRedisStreamMessageListener<?>> listeners; // 消息监听器列表
    private final RedisMQTemplate redisTemplate; // Redis 消息队列模板
    private final String groupName; // 消费者组名
    private final RedissonClient redissonClient; // Redisson 客户端，用于分布式锁

    /**
     * 一分钟执行一次,这里选择每分钟的35秒执行，是为了避免整点任务过多的问题
     */
    @Scheduled(cron = "35 * * * * ?")
    public void messageResend() {
        RLock lock = redissonClient.getLock(LOCK_KEY); // 获取分布式锁
        // 尝试加锁
        if (lock.tryLock()) {
            try {
                execute(); // 执行消息重投递逻辑
            } catch (Exception ex) {
                log.error("[messageResend][执行异常]", ex); // 异常处理
            } finally {
                lock.unlock(); // 释放锁
            }
        }
    }

    /**
     * 执行清理逻辑
     *
     * @see <a href="https://gitee.com/zhijiantianya/engine/pulls/480/files">讨论</a>
     */
    private void execute() {
        StreamOperations<String, Object, Object> ops = redisTemplate.getRedisTemplate().opsForStream(); // 获取 Stream 操作对象
        listeners.forEach(listener -> {
            // 获取当前流的待处理消息摘要
            PendingMessagesSummary pendingMessagesSummary = Objects.requireNonNull(ops.pending(listener.getStreamKey(), groupName));
            // 获取每个消费者的待处理消息数量
            Map<String, Long> pendingMessagesPerConsumer = pendingMessagesSummary.getPendingMessagesPerConsumer();
            pendingMessagesPerConsumer.forEach((consumerName, pendingMessageCount) -> {
                log.info("[processPendingMessage][消费者({}) 消息数量({})]", consumerName, pendingMessageCount);
                // 获取每个消费者的待处理消息的详情信息
                PendingMessages pendingMessages = ops.pending(listener.getStreamKey(), Consumer.from(groupName, consumerName), Range.unbounded(), pendingMessageCount);
                if (pendingMessages.isEmpty()) {
                    return;
                }
                pendingMessages.forEach(pendingMessage -> {
                    // 获取消息上一次传递到 consumer 的时间
                    long lastDelivery = pendingMessage.getElapsedTimeSinceLastDelivery().getSeconds();
                    if (lastDelivery < EXPIRE_TIME) {
                        return; // 如果消息未超时，则跳过
                    }
                    // 获取指定 id 的消息体
                    List<MapRecord<String, Object, Object>> records = ops.range(listener.getStreamKey(),
                            Range.of(Range.Bound.inclusive(pendingMessage.getIdAsString()), Range.Bound.inclusive(pendingMessage.getIdAsString())));
                    if (CollUtil.isEmpty(records)) {
                        return; // 如果消息不存在，则跳过
                    }
                    // 重新投递消息
                    redisTemplate.getRedisTemplate().opsForStream().add(StreamRecords.newRecord()
                            .ofObject(records.get(0).getValue()) // 设置消息内容
                            .withStreamKey(listener.getStreamKey())); // 设置流的 key
                    // 确认消息已消费完成
                    redisTemplate.getRedisTemplate().opsForStream().acknowledge(groupName, records.get(0));
                    log.info("[processPendingMessage][消息({})重新投递成功]", records.get(0).getId());
                });
            });
        });
    }
}
