package org.nstep.engine.module.system.api.social;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.social.dto.SocialUserBindReqDTO;
import org.nstep.engine.module.system.api.social.dto.SocialUserRespDTO;
import org.nstep.engine.module.system.api.social.dto.SocialUserUnbindReqDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * 社交用户的 RPC 服务接口
 * <p>
 * 该接口提供了与社交用户相关的 API，包括绑定、取消绑定、通过用户 ID 或授权码获取社交用户等功能。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 社交用户")
public interface SocialUserApi {

    // 定义请求的前缀
    String PREFIX = ApiConstants.PREFIX + "/social-user";

    /**
     * 绑定社交用户
     * <p>
     * 用于将社交用户与系统用户绑定，提供社交用户的详细信息。
     *
     * @param reqDTO 请求参数，包含绑定社交用户所需的信息
     * @return 返回绑定操作的结果
     */
    @PostMapping(PREFIX + "/bind")
    @Operation(summary = "绑定社交用户")
    CommonResult<String> bindSocialUser(@Valid @RequestBody SocialUserBindReqDTO reqDTO);

    /**
     * 取消绑定社交用户
     * <p>
     * 用于取消已绑定的社交用户与系统用户的绑定关系。
     *
     * @param reqDTO 请求参数，包含取消绑定所需的信息
     * @return 返回取消绑定操作的结果
     */
    @DeleteMapping(PREFIX + "/unbind")
    @Operation(summary = "取消绑定社交用户")
    CommonResult<Boolean> unbindSocialUser(@Valid @RequestBody SocialUserUnbindReqDTO reqDTO);

    /**
     * 通过用户 ID 获取社交用户信息
     * <p>
     * 用于通过系统用户 ID 获取对应的社交用户信息。
     *
     * @param userType   用户类型
     * @param userId     用户编号
     * @param socialType 社交平台的类型
     * @return 返回社交用户的信息
     */
    @GetMapping(PREFIX + "/get-by-user-id")
    @Operation(summary = "获得社交用户，基于 userId")
    @Parameters({
            @Parameter(name = "userType", description = "用户类型", example = "2", required = true),
            @Parameter(name = "userId", description = "用户编号", example = "1024", required = true),
            @Parameter(name = "socialType", description = "社交平台的类型", example = "1", required = true),
    })
    CommonResult<SocialUserRespDTO> getSocialUserByUserId(@RequestParam("userType") Integer userType,
                                                          @RequestParam("userId") Long userId,
                                                          @RequestParam("socialType") Integer socialType);

    /**
     * 通过授权码获取社交用户信息
     * <p>
     * 用于通过授权码获取社交用户的信息，通常用于 OAuth 认证流程。
     *
     * @param userType   用户类型
     * @param socialType 社交平台的类型
     * @param code       授权码
     * @param state      状态信息
     * @return 返回社交用户的信息
     */
    @GetMapping(PREFIX + "/get-by-code")
    @Operation(summary = "获得社交用户，基于授权码")
    @Parameters({
            @Parameter(name = "userType", description = "用户类型", example = "2", required = true),
            @Parameter(name = "socialType", description = "社交平台的类型", example = "1", required = true),
            @Parameter(name = "code", description = "授权码", example = "88888", required = true),
            @Parameter(name = "state", description = "状态信息", example = "666", required = true),
    })
    CommonResult<SocialUserRespDTO> getSocialUserByCode(@RequestParam("userType") Integer userType,
                                                        @RequestParam("socialType") Integer socialType,
                                                        @RequestParam("code") String code,
                                                        @RequestParam("state") String state);

}
