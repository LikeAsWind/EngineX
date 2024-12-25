package org.nstep.engine.module.system.api.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 社交用户的响应 DTO
 * <p>
 * 该类用于返回社交用户的信息，包括社交平台的 openid、昵称、头像以及关联的用户编号等。
 */
@Schema(description = "RPC 服务 - 社交用户 Response DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocialUserRespDTO {

    /**
     * 社交用户的 openid
     * <p>
     * 用于唯一标识社交平台上的用户。
     */
    @Schema(description = "社交用户 openid", requiredMode = Schema.RequiredMode.REQUIRED, example = "zsw")
    private String openid;

    /**
     * 社交用户的昵称
     * <p>
     * 社交平台上用户的昵称。
     */
    @Schema(description = "社交用户的昵称", requiredMode = Schema.RequiredMode.REQUIRED, example = "engine源码")
    private String nickname;

    /**
     * 社交用户的头像
     * <p>
     * 社交平台上用户的头像 URL 地址。
     */
    @Schema(description = "社交用户的头像", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.nstep.cn/1.jpg")
    private String avatar;

    /**
     * 关联的用户编号
     * <p>
     * 与社交用户关联的系统用户编号。
     */
    @Schema(description = "关联的用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long userId;

}
