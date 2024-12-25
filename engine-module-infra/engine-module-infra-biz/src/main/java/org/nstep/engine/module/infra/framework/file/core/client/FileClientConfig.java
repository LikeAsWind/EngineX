package org.nstep.engine.module.infra.framework.file.core.client;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 文件客户端的配置接口。
 * 不同实现的文件客户端需要不同的配置。通过子类来定义具体的配置内容。
 * 使用 Jackson 的 @JsonTypeInfo 注解来支持多态序列化和反序列化。
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS)
// @JsonTypeInfo 注解的作用，Jackson 多态支持
// 1. 在序列化时，将类名作为 @class 属性添加到 JSON 中。
// 2. 在反序列化时，Jackson 会根据 @class 属性的值创建出正确的类实例。
public interface FileClientConfig {
}
