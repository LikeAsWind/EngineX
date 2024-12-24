package org.nstep.engine.framework.websocket.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * WebSocket 配置项类。
 * <p>
 * 提供 WebSocket 相关的配置属性，通过 `engine.websocket` 前缀进行绑定。
 * 使用 Spring Boot 的 `@ConfigurationProperties` 和 `@Validated` 注解，
 * 实现配置属性的自动注入与校验。
 */
@ConfigurationProperties("engine.websocket") // 绑定配置前缀为 engine.websocket
@Data // 自动生成 getter/setter、toString 等方法
@Validated // 启用 JSR-303 校验
public class WebSocketProperties {

    /**
     * WebSocket 的连接路径。
     * <p>
     * 用于定义 WebSocket 服务的访问路径，默认值为 "/ws"。
     * 例如：如果部署在 <a href =http://localhost:8080></a>，则完整路径为 <a href =http://localhost:8080/ws</a>。
     * <p>
     * 校验规则：
     * - 不能为空
     */
    @NotEmpty(message = "WebSocket 的连接路径不能为空")
    private String path = "/ws";

    /**
     * 消息发送器的类型。
     * <p>
     * 用于指定 WebSocket 消息的发送方式。
     * 支持以下选项：
     * - `local`: 使用本地发送器。
     * - `redis`: 使用 Redis 作为消息中间件。
     * - `rocketmq`: 使用 RocketMQ 作为消息中间件。
     * - `kafka`: 使用 Kafka 作为消息中间件。
     * - `rabbitmq`: 使用 RabbitMQ 作为消息中间件。
     * <p>
     * 校验规则：
     * - 不能为空
     * 默认值为 `local`。
     */
    @NotNull(message = "WebSocket 的消息发送者不能为空")
    private String senderType = "local";

}
