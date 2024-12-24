package org.nstep.engine.framework.websocket.config;

import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.nstep.engine.framework.mq.redis.config.EngineRedisMQConsumerAutoConfiguration;
import org.nstep.engine.framework.mq.redis.core.RedisMQTemplate;
import org.nstep.engine.framework.websocket.core.handler.JsonWebSocketMessageHandler;
import org.nstep.engine.framework.websocket.core.listener.WebSocketMessageListener;
import org.nstep.engine.framework.websocket.core.security.LoginUserHandshakeInterceptor;
import org.nstep.engine.framework.websocket.core.security.WebSocketAuthorizeRequestsCustomizer;
import org.nstep.engine.framework.websocket.core.sender.kafka.KafkaWebSocketMessageConsumer;
import org.nstep.engine.framework.websocket.core.sender.kafka.KafkaWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.local.LocalWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.rabbitmq.RabbitMQWebSocketMessageConsumer;
import org.nstep.engine.framework.websocket.core.sender.rabbitmq.RabbitMQWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.redis.RedisWebSocketMessageConsumer;
import org.nstep.engine.framework.websocket.core.sender.redis.RedisWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.sender.rocketmq.RocketMQWebSocketMessageConsumer;
import org.nstep.engine.framework.websocket.core.sender.rocketmq.RocketMQWebSocketMessageSender;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionHandlerDecorator;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionManager;
import org.nstep.engine.framework.websocket.core.session.WebSocketSessionManagerImpl;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.List;

/**
 * WebSocket 自动配置类。
 * <p>
 * 提供 WebSocket 的自动化配置，支持多种消息发送方式（本地、Redis、RocketMQ、RabbitMQ、Kafka）。
 * 通过注解和条件配置，动态启用或禁用相关功能。
 */
@AutoConfiguration(before = EngineRedisMQConsumerAutoConfiguration.class)
// 确保在 EngineRedisMQConsumerAutoConfiguration 之前加载，以便 RedisWebSocketMessageConsumer 优先创建。
@EnableWebSocket // 启用 WebSocket 功能
@ConditionalOnProperty(prefix = "engine.websocket", value = "enable", matchIfMissing = true)
// 如果 engine.websocket.enable 未设置或为 true，则启用 WebSocket 配置。
@EnableConfigurationProperties(WebSocketProperties.class)
// 将 WebSocketProperties 配置类注入到 Spring 容器中。
public class EngineWebSocketAutoConfiguration {

    /**
     * 配置 WebSocket 服务。
     *
     * @param handshakeInterceptors WebSocket 握手拦截器
     * @param webSocketHandler      WebSocket 消息处理器
     * @param webSocketProperties   WebSocket 配置属性
     * @return WebSocketConfigurer 实例
     */
    @Bean
    public WebSocketConfigurer webSocketConfigurer(HandshakeInterceptor[] handshakeInterceptors, WebSocketHandler webSocketHandler, WebSocketProperties webSocketProperties) {
        return registry -> registry.addHandler(webSocketHandler, webSocketProperties.getPath()) // 注册 WebSocketHandler
                .addInterceptors(handshakeInterceptors) // 添加拦截器
                .setAllowedOriginPatterns("*"); // 允许跨域访问
    }

    /**
     * 配置默认的握手拦截器。
     *
     * @return HandshakeInterceptor 实例
     */
    @Bean
    public HandshakeInterceptor handshakeInterceptor() {
        return new LoginUserHandshakeInterceptor();
    }

    /**
     * 配置 WebSocket 消息处理器。
     *
     * @param sessionManager   WebSocket 会话管理器
     * @param messageListeners 消息监听器列表
     * @return WebSocketHandler 实例
     */
    @Bean
    public WebSocketHandler webSocketHandler(WebSocketSessionManager sessionManager, List<? extends WebSocketMessageListener<?>> messageListeners) {
        JsonWebSocketMessageHandler messageHandler = new JsonWebSocketMessageHandler(messageListeners);
        return new WebSocketSessionHandlerDecorator(messageHandler, sessionManager);
    }

    /**
     * 配置 WebSocket 会话管理器。
     *
     * @return WebSocketSessionManager 实例
     */
    @Bean
    public WebSocketSessionManager webSocketSessionManager() {
        return new WebSocketSessionManagerImpl();
    }

    /**
     * 配置 WebSocket 授权请求自定义器。
     *
     * @param webSocketProperties WebSocket 配置属性
     * @return WebSocketAuthorizeRequestsCustomizer 实例
     */
    @Bean
    public WebSocketAuthorizeRequestsCustomizer webSocketAuthorizeRequestsCustomizer(WebSocketProperties webSocketProperties) {
        return new WebSocketAuthorizeRequestsCustomizer(webSocketProperties);
    }

    // ==================== Sender 相关配置 ====================

    /**
     * 本地消息发送配置。
     */
    @Configuration
    @ConditionalOnProperty(prefix = "engine.websocket", name = "sender-type", havingValue = "local")
    public class LocalWebSocketMessageSenderConfiguration {

        @Bean
        public LocalWebSocketMessageSender localWebSocketMessageSender(WebSocketSessionManager sessionManager) {
            return new LocalWebSocketMessageSender(sessionManager);
        }

    }

    /**
     * Redis 消息发送配置。
     */
    @Configuration
    @ConditionalOnProperty(prefix = "engine.websocket", name = "sender-type", havingValue = "redis")
    public class RedisWebSocketMessageSenderConfiguration {

        @Bean
        public RedisWebSocketMessageSender redisWebSocketMessageSender(WebSocketSessionManager sessionManager, RedisMQTemplate redisMQTemplate) {
            return new RedisWebSocketMessageSender(sessionManager, redisMQTemplate);
        }

        @Bean
        public RedisWebSocketMessageConsumer redisWebSocketMessageConsumer(RedisWebSocketMessageSender redisWebSocketMessageSender) {
            return new RedisWebSocketMessageConsumer(redisWebSocketMessageSender);
        }

    }

    /**
     * RocketMQ 消息发送配置。
     */
    @Configuration
    @ConditionalOnProperty(prefix = "engine.websocket", name = "sender-type", havingValue = "rocketmq")
    public class RocketMQWebSocketMessageSenderConfiguration {

        @Bean
        public RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender(WebSocketSessionManager sessionManager, RocketMQTemplate rocketMQTemplate, @Value("${engine.websocket.sender-rocketmq.topic}") String topic) {
            return new RocketMQWebSocketMessageSender(sessionManager, rocketMQTemplate, topic);
        }

        @Bean
        public RocketMQWebSocketMessageConsumer rocketMQWebSocketMessageConsumer(RocketMQWebSocketMessageSender rocketMQWebSocketMessageSender) {
            return new RocketMQWebSocketMessageConsumer(rocketMQWebSocketMessageSender);
        }

    }

    /**
     * RabbitMQ 消息发送配置。
     */
    @Configuration
    @ConditionalOnProperty(prefix = "engine.websocket", name = "sender-type", havingValue = "rabbitmq")
    public class RabbitMQWebSocketMessageSenderConfiguration {

        @Bean
        public RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender(WebSocketSessionManager sessionManager, RabbitTemplate rabbitTemplate, TopicExchange websocketTopicExchange) {
            return new RabbitMQWebSocketMessageSender(sessionManager, rabbitTemplate, websocketTopicExchange);
        }

        @Bean
        public RabbitMQWebSocketMessageConsumer rabbitMQWebSocketMessageConsumer(RabbitMQWebSocketMessageSender rabbitMQWebSocketMessageSender) {
            return new RabbitMQWebSocketMessageConsumer(rabbitMQWebSocketMessageSender);
        }

        /**
         * 创建 Topic Exchange。
         *
         * @param exchange 交换机名称
         * @return TopicExchange 实例
         */
        @Bean
        public TopicExchange websocketTopicExchange(@Value("${engine.websocket.sender-rabbitmq.exchange}") String exchange) {
            return new TopicExchange(exchange, true, false);
        }

    }

    /**
     * Kafka 消息发送配置。
     */
    @Configuration
    @ConditionalOnProperty(prefix = "engine.websocket", name = "sender-type", havingValue = "kafka")
    public class KafkaWebSocketMessageSenderConfiguration {

        @Bean
        public KafkaWebSocketMessageSender kafkaWebSocketMessageSender(WebSocketSessionManager sessionManager, KafkaTemplate<Object, Object> kafkaTemplate, @Value("${engine.websocket.sender-kafka.topic}") String topic) {
            return new KafkaWebSocketMessageSender(sessionManager, kafkaTemplate, topic);
        }

        @Bean
        public KafkaWebSocketMessageConsumer kafkaWebSocketMessageConsumer(KafkaWebSocketMessageSender kafkaWebSocketMessageSender) {
            return new KafkaWebSocketMessageConsumer(kafkaWebSocketMessageSender);
        }

    }

}
