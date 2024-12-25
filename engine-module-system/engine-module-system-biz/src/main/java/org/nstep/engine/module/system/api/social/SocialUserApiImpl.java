package org.nstep.engine.module.system.api.social;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.social.dto.SocialUserBindReqDTO;
import org.nstep.engine.module.system.api.social.dto.SocialUserRespDTO;
import org.nstep.engine.module.system.api.social.dto.SocialUserUnbindReqDTO;
import org.nstep.engine.module.system.service.social.SocialUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 社交用户 API 实现类
 * <p>
 * 该类提供了与社交用户相关的 RESTful API 接口，
 * 包括绑定社交用户、解绑社交用户、通过用户 ID 或授权码获取社交用户信息等功能。
 * 提供给 Feign 调用，处理社交用户相关的业务逻辑。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated
public class SocialUserApiImpl implements SocialUserApi {

    @Resource
    private SocialUserService socialUserService; // 社交用户服务，处理社交用户相关的业务逻辑

    /**
     * 绑定社交用户
     *
     * @param reqDTO 绑定社交用户的请求参数
     * @return 绑定结果
     */
    @Override
    public CommonResult<String> bindSocialUser(SocialUserBindReqDTO reqDTO) {
        return success(socialUserService.bindSocialUser(reqDTO));
    }

    /**
     * 解绑社交用户
     *
     * @param reqDTO 解绑社交用户的请求参数
     * @return 解绑结果
     */
    @Override
    public CommonResult<Boolean> unbindSocialUser(SocialUserUnbindReqDTO reqDTO) {
        socialUserService.unbindSocialUser(reqDTO.getUserId(), reqDTO.getUserType(),
                reqDTO.getSocialType(), reqDTO.getOpenid());
        return success(true);
    }

    /**
     * 根据用户 ID 获取社交用户信息
     *
     * @param userType   用户类型
     * @param userId     用户 ID
     * @param socialType 社交平台类型
     * @return 社交用户信息
     */
    @Override
    public CommonResult<SocialUserRespDTO> getSocialUserByUserId(Integer userType, Long userId, Integer socialType) {
        return success(socialUserService.getSocialUserByUserId(userType, userId, socialType));
    }

    /**
     * 根据授权码获取社交用户信息
     *
     * @param userType   用户类型
     * @param socialType 社交平台类型
     * @param code       授权码
     * @param state      状态信息
     * @return 社交用户信息
     */
    @Override
    public CommonResult<SocialUserRespDTO> getSocialUserByCode(Integer userType, Integer socialType, String code, String state) {
        return success(socialUserService.getSocialUserByCode(userType, socialType, code, state));
    }

}
