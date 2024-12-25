package org.nstep.engine.module.system.controller.admin.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 管理后台 - 登录用户的菜单信息 Response VO
 * <p>
 * 该类用于封装登录用户的菜单信息，包括菜单的名称、路由地址、组件路径、是否可见等信息。
 * 菜单信息是基于用户角色权限的，因此每个用户的菜单信息可能不同。
 */
@Schema(description = "管理后台 - 登录用户的菜单信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthMenuRespVO {

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
     * 菜单的显示名称，通常为文本
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
     * 菜单的组件名称，用于在前端页面渲染时确定显示的组件
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
    private List<AuthMenuRespVO> children;

}
