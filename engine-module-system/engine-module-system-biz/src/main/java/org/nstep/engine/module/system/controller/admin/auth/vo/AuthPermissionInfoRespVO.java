package org.nstep.engine.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

/**
 * 管理后台 - 登录用户的权限信息 Response VO，额外包括用户信息和角色列表
 * <p>
 * 该类用于封装登录用户的权限信息，包括用户的基本信息、角色、操作权限、菜单信息等。
 * 它是管理后台用户权限相关信息的核心数据结构。
 */
@Schema(description = "管理后台 - 登录用户的权限信息 Response VO，额外包括用户信息和角色列表")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthPermissionInfoRespVO {

    /**
     * 用户信息
     * 包含用户的基本信息，如用户 ID、昵称、头像、部门 ID 等
     */
    @Schema(description = "用户信息", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserVO user;

    /**
     * 角色标识数组
     * 用户的角色集合，每个角色标识符用于表示用户的权限范围
     */
    @Schema(description = "角色标识数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> roles;

    /**
     * 操作权限数组
     * 用户的权限集合，每个权限标识符用于表示用户可以执行的操作
     */
    @Schema(description = "操作权限数组", requiredMode = Schema.RequiredMode.REQUIRED)
    private Set<String> permissions;

    /**
     * 菜单树
     * 用户有权限访问的菜单结构，通常以树形结构表示
     */
    @Schema(description = "菜单树", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<MenuVO> menus;

    /**
     * 用户信息 VO
     * <p>
     * 用于封装用户的详细信息，包括用户 ID、昵称、头像和部门 ID。
     */
    @Schema(description = "用户信息 VO")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserVO {

        /**
         * 用户编号
         * 用户的唯一标识符
         */
        @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        private Long id;

        /**
         * 用户昵称
         * 用户的显示名称或昵称
         */
        @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine源码")
        private String nickname;

        /**
         * 用户头像
         * 用户的头像 URL 地址
         */
        @Schema(description = "用户头像", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.nstep.cn/xx.jpg")
        private String avatar;

        /**
         * 部门编号
         * 用户所在部门的唯一标识符
         */
        @Schema(description = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "2048")
        private Long deptId;

    }

    /**
     * 菜单信息 VO
     * <p>
     * 用于封装菜单的详细信息，包括菜单的 ID、名称、路径、图标等。
     * 该类支持嵌套子菜单，形成树形结构。
     */
    @Schema(description = "管理后台 - 登录用户的菜单信息 Response VO")
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MenuVO {

        /**
         * 菜单 ID
         * 菜单的唯一标识符
         */
        @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
        private Long id;

        /**
         * 父菜单 ID
         * 如果该菜单是子菜单，则该字段为父菜单的 ID
         */
        @Schema(description = "父菜单 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
        private Long parentId;

        /**
         * 菜单名称
         * 菜单的显示名称
         */
        @Schema(description = "菜单名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine")
        private String name;

        /**
         * 路由地址
         * 仅菜单类型为菜单或目录时需要传递，表示该菜单对应的路由地址
         */
        @Schema(description = "路由地址,仅菜单类型为菜单或者目录时，才需要传", example = "post")
        private String path;

        /**
         * 组件路径
         * 仅菜单类型为菜单时需要传递，表示该菜单对应的组件路径
         */
        @Schema(description = "组件路径,仅菜单类型为菜单时，才需要传", example = "system/post/index")
        private String component;

        /**
         * 组件名
         * 菜单的组件名称，用于前端渲染时确定显示的组件
         */
        @Schema(description = "组件名", example = "SystemUser")
        private String componentName;

        /**
         * 菜单图标
         * 仅菜单类型为菜单或目录时需要传递，表示该菜单的图标
         */
        @Schema(description = "菜单图标,仅菜单类型为菜单或者目录时，才需要传", example = "/menu/list")
        private String icon;

        /**
         * 是否可见
         * 标记该菜单是否对用户可见，通常用于权限控制
         */
        @Schema(description = "是否可见", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
        private Boolean visible;

        /**
         * 是否缓存
         * 标记该菜单是否应该被缓存，通常用于性能优化
         */
        @Schema(description = "是否缓存", requiredMode = Schema.RequiredMode.REQUIRED, example = "false")
        private Boolean keepAlive;

        /**
         * 是否总是显示
         * 标记该菜单是否总是显示，通常用于父子菜单的显示控制
         */
        @Schema(description = "是否总是显示", example = "false")
        private Boolean alwaysShow;

        /**
         * 子路由
         * 如果该菜单有子菜单，则该字段包含子菜单的列表
         */
        private List<MenuVO> children;

    }

}
