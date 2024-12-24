package org.nstep.engine.framework.security.core;

import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.nstep.engine.framework.common.enums.UserTypeEnum;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录用户信息
 * <p>
 * 该类表示登录用户的相关信息，包括用户编号、用户类型、用户信息、授权范围、租户编号等。
 */
@Data
public class LoginUser {

    // 用户信息中的常量字段，用于存储用户的昵称和部门 ID
    public static final String INFO_KEY_NICKNAME = "nickname";
    public static final String INFO_KEY_DEPT_ID = "deptId";

    /**
     * 用户编号
     * <p>
     * 唯一标识一个用户的 ID
     */
    private Long id;

    /**
     * 用户类型
     * <p>
     * 关联 {@link UserTypeEnum} 枚举，表示用户的类型，例如管理员、普通用户等。
     */
    private Integer userType;

    /**
     * 额外的用户信息
     * <p>
     * 存储一些不常用的、附加的用户信息，使用键值对形式存储。
     */
    private Map<String, String> info;

    /**
     * 租户编号
     * <p>
     * 表示当前用户所属的租户的 ID，通常用于多租户系统。
     */
    private Long tenantId;

    /**
     * 授权范围
     * <p>
     * 用户的授权范围，可以是权限、资源等的标识符列表，通常用于权限控制。
     */
    private List<String> scopes;

    /**
     * 过期时间
     * <p>
     * 表示当前用户的会话或令牌的过期时间。
     */
    private LocalDateTime expiresTime;

    // ========== 上下文 ==========

    /**
     * 上下文字段，不进行持久化
     * <p>
     * 用于基于 LoginUser 维度的临时缓存，存储一些与用户相关的临时数据。
     * 这些数据不会被持久化到数据库中，只会在当前会话中有效。
     */
    @JsonIgnore
    private Map<String, Object> context;

    /**
     * 设置上下文中的某个字段
     *
     * @param key   上下文字段的键
     * @param value 上下文字段的值
     */
    public void setContext(String key, Object value) {
        if (context == null) {
            context = new HashMap<>();
        }
        context.put(key, value);
    }

    /**
     * 获取上下文中的某个字段
     *
     * @param key  上下文字段的键
     * @param type 字段的类型
     * @param <T>  字段的类型
     * @return 上下文字段的值
     */
    public <T> T getContext(String key, Class<T> type) {
        return MapUtil.get(context, key, type);
    }

}
