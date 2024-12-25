package org.nstep.engine.module.system.api.permission.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * 部门数据权限响应数据传输对象（DTO）
 * <p>
 * 该类用于返回部门数据权限的相关信息，包括是否有权限查看全部数据、查看自己数据的权限以及可查看的部门编号列表。
 */
@Schema(description = "RPC 服务 - 部门的数据权限 Response DTO")
@Data
public class DeptDataPermissionRespDTO {

    /**
     * 是否可查看全部数据
     * <p>
     * 该字段表示当前用户是否拥有查看所有部门数据的权限。
     * 如果为 `true`，则用户可以查看所有部门的数据；如果为 `false`，则不能查看所有部门的数据。
     */
    @Schema(description = "是否可查看全部数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean all;

    /**
     * 是否可查看自己的数据
     * <p>
     * 该字段表示当前用户是否拥有查看自己所属部门数据的权限。
     * 如果为 `true`，则用户可以查看自己所属部门的数据；如果为 `false`，则不能查看自己部门的数据。
     */
    @Schema(description = "是否可查看自己的数据", requiredMode = Schema.RequiredMode.REQUIRED, example = "true")
    private Boolean self;

    /**
     * 可查看的部门编号数组
     * <p>
     * 该字段表示当前用户可以查看的具体部门编号列表。如果用户拥有多个部门的查看权限，则该字段包含多个部门编号。
     * 该字段是一个集合类型，支持多个部门编号。
     */
    @Schema(description = "可查看的部门编号数组", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 3]")
    private Set<Long> deptIds;

    /**
     * 默认构造函数
     * <p>
     * 初始化 `all` 和 `self` 为 `false`，并初始化 `deptIds` 为一个空的 `HashSet`。
     */
    public DeptDataPermissionRespDTO() {
        this.all = false;  // 默认不可查看全部数据
        this.self = false; // 默认不可查看自己的数据
        this.deptIds = new HashSet<>(); // 初始化为空的部门编号集合
    }

}
