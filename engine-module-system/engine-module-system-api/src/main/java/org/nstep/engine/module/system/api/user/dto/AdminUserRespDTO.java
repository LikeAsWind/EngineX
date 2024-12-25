package org.nstep.engine.module.system.api.user.dto;

import com.fhs.core.trans.vo.VO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Set;

/**
 * 管理员用户的响应数据传输对象（DTO）
 * <p>
 * 该类用于表示从系统获取的管理员用户的详细信息，包括用户的 ID、昵称、状态、部门信息、岗位、手机号码等。
 */
@Schema(description = "RPC 服务 - Admin 用户 Response DTO")
@Data
public class AdminUserRespDTO implements VO {

    /**
     * 用户 ID
     * <p>
     * 唯一标识一个管理员用户的 ID。
     */
    @Schema(description = "用户 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    /**
     * 用户昵称
     * <p>
     * 管理员用户的昵称，用于显示在系统中。
     */
    @Schema(description = "用户昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "小王")
    private String nickname;

    /**
     * 帐号状态
     * <p>
     * 用户的当前状态，参见 `CommonStatusEnum` 枚举（例如：1 表示启用，0 表示禁用）。
     */
    @Schema(description = "帐号状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Integer status; // 参见 CommonStatusEnum 枚举

    /**
     * 部门编号
     * <p>
     * 用户所在的部门编号。
     */
    @Schema(description = "部门编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long deptId;

    /**
     * 岗位编号数组
     * <p>
     * 用户的岗位编号集合，表示用户所担任的岗位。
     */
    @Schema(description = "岗位编号数组", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 3]")
    private Set<Long> postIds;

    /**
     * 手机号码
     * <p>
     * 用户的手机号，用于联系管理员用户。
     */
    @Schema(description = "手机号码", requiredMode = Schema.RequiredMode.REQUIRED, example = "15601691300")
    private String mobile;

    /**
     * 用户头像
     * <p>
     * 用户的头像图片 URL。
     */
    @Schema(description = "用户头像", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.nstep.cn/1.png")
    private String avatar;

}
