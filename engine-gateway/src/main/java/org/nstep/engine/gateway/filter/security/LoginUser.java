package org.nstep.engine.gateway.filter.security;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 登录用户信息类
 * <p>
 * 该类用于表示登录用户的详细信息，包括用户的基本信息、租户信息、授权范围、过期时间等。
 * 该类从 `engine-spring-boot-starter-security` 模块的 `LoginUser` 类复制而来，提供了用户登录时的核心数据。
 */
@Data
public class LoginUser {

    /**
     * 用户编号
     * <p>
     * 唯一标识一个用户的编号，通常为数据库中的主键。
     */
    private Long id;

    /**
     * 用户类型
     * <p>
     * 用于区分不同类型的用户，例如管理员、普通用户等。可以根据具体业务需求定义不同的用户类型。
     */
    private Integer userType;

    /**
     * 额外的用户信息
     * <p>
     * 存储用户的其他信息，以键值对的形式存储。可以包含如用户名、邮箱、手机号等信息。
     */
    private Map<String, String> info;

    /**
     * 租户编号
     * <p>
     * 用于区分不同租户的数据，支持多租户系统，每个租户可以有不同的用户。
     */
    private Long tenantId;

    /**
     * 授权范围
     * <p>
     * 表示用户的授权范围，可以是一个或多个权限标识符（例如角色、资源权限等）。
     * 用于控制用户对系统资源的访问权限。
     */
    private List<String> scopes;

    /**
     * 过期时间
     * <p>
     * 用户登录信息的过期时间，用于控制登录状态的有效期。如果当前时间超过该时间，用户的登录状态将失效。
     */
    private LocalDateTime expiresTime;

}
