package org.nstep.engine.framework.security.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collections;
import java.util.List;

/**
 * 安全配置属性类，包含与安全相关的配置项
 * <p>
 * 该类用于从配置文件中加载安全相关的配置项，如访问令牌的请求头、请求参数、mock 模式开关等。
 * 配置项的值会根据前缀 "engine.security" 自动映射到类的属性中。
 * </p>
 */
@ConfigurationProperties(prefix = "engine.security") // 从配置文件加载以 "engine.security" 为前缀的属性
@Validated // 启用验证机制，确保配置项的合法性
@Data // 自动生成 getter、setter、toString、equals、hashCode 方法
public class SecurityProperties {

    /**
     * HTTP 请求时，访问令牌的请求 Header
     * <p>
     * 该配置项指定了在 HTTP 请求头中传递访问令牌的字段名，默认是 "Authorization"。
     * </p>
     */
    @NotEmpty(message = "Token Header 不能为空") // 确保该字段不能为空
    private String tokenHeader = "Authorization"; // 默认值为 "Authorization"

    /**
     * HTTP 请求时，访问令牌的请求参数
     * <p>
     * 该配置项指定了在 HTTP 请求的 URL 参数中传递访问令牌的字段名，默认是 "token"。
     * 主要用于解决 WebSocket 无法通过 Header 传递令牌的问题。
     * </p>
     */
    @NotEmpty(message = "Token Parameter 不能为空") // 确保该字段不能为空
    private String tokenParameter = "token"; // 默认值为 "token"

    /**
     * mock 模式的开关
     * <p>
     * 该配置项控制是否启用 mock 模式，mock 模式可以用于模拟认证过程，方便开发和测试。
     * </p>
     */
    @NotNull(message = "mock 模式的开关不能为空") // 确保该字段不能为空
    private Boolean mockEnable = false; // 默认值为 false

    /**
     * mock 模式的密钥
     * <p>
     * 该配置项用于 mock 模式时的密钥，确保 mock 模式的安全性。
     * 只有在 mockEnable 为 true 时，才需要配置该密钥。
     * </p>
     */
    @NotEmpty(message = "mock 模式的密钥不能为空") // 确保该字段不能为空
    private String mockSecret = "test"; // 默认值为 "test"

    /**
     * 免登录的 URL 列表
     * <p>
     * 该配置项指定了哪些 URL 路径可以无需登录即可访问。通常用于配置公共资源、登录页面等。
     * </p>
     */
    private List<String> permitAllUrls = Collections.emptyList(); // 默认值为空列表

    /**
     * PasswordEncoder 加密复杂度，越高开销越大
     * <p>
     * 该配置项控制 BCrypt 密码加密器的复杂度，值越大，密码加密的开销越大，安全性也越高。
     * </p>
     */
    private Integer passwordEncoderLength = 4; // 默认值为 4
}
