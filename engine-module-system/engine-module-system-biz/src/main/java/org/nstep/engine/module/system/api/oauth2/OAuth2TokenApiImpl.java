package org.nstep.engine.module.system.api.oauth2;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.system.api.oauth2.dto.OAuth2AccessTokenCheckRespDTO;
import org.nstep.engine.module.system.api.oauth2.dto.OAuth2AccessTokenCreateReqDTO;
import org.nstep.engine.module.system.api.oauth2.dto.OAuth2AccessTokenRespDTO;
import org.nstep.engine.module.system.dal.dataobject.oauth2.OAuth2AccessTokenDO;
import org.nstep.engine.module.system.service.oauth2.OAuth2TokenService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * OAuth2 令牌管理 API 实现类
 * <p>
 * 该类提供了 OAuth2 令牌相关的 RESTful API 接口，包括创建、校验、删除和刷新访问令牌。
 * 提供给 Feign 调用，处理与 OAuth2 令牌相关的业务逻辑。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class OAuth2TokenApiImpl implements OAuth2TokenApi {

    @Resource
    private OAuth2TokenService oauth2TokenService; // OAuth2 令牌服务，处理令牌的创建、校验、删除和刷新

    /**
     * 创建 OAuth2 访问令牌
     *
     * @param reqDTO 请求 DTO，包含用户 ID、用户类型、客户端 ID 和权限范围
     * @return 创建的 OAuth2 访问令牌响应 DTO
     */
    @Override
    public CommonResult<OAuth2AccessTokenRespDTO> createAccessToken(OAuth2AccessTokenCreateReqDTO reqDTO) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.createAccessToken(
                reqDTO.getUserId(), reqDTO.getUserType(), reqDTO.getClientId(), reqDTO.getScopes());
        // 将创建的 OAuth2 访问令牌转换为响应 DTO 并返回
        return success(BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRespDTO.class));
    }

    /**
     * 校验 OAuth2 访问令牌的有效性
     *
     * @param accessToken 访问令牌
     * @return 校验结果的响应 DTO，包含令牌信息
     */
    @Override
    public CommonResult<OAuth2AccessTokenCheckRespDTO> checkAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.checkAccessToken(accessToken);
        // 将校验结果转换为响应 DTO 并返回
        return success(BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenCheckRespDTO.class));
    }

    /**
     * 删除 OAuth2 访问令牌
     *
     * @param accessToken 访问令牌
     * @return 删除后的 OAuth2 访问令牌响应 DTO
     */
    @Override
    public CommonResult<OAuth2AccessTokenRespDTO> removeAccessToken(String accessToken) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.removeAccessToken(accessToken);
        // 将删除后的 OAuth2 访问令牌转换为响应 DTO 并返回
        return success(BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRespDTO.class));
    }

    /**
     * 刷新 OAuth2 访问令牌
     *
     * @param refreshToken 刷新令牌
     * @param clientId     客户端 ID
     * @return 刷新后的 OAuth2 访问令牌响应 DTO
     */
    @Override
    public CommonResult<OAuth2AccessTokenRespDTO> refreshAccessToken(String refreshToken, String clientId) {
        OAuth2AccessTokenDO accessTokenDO = oauth2TokenService.refreshAccessToken(refreshToken, clientId);
        // 将刷新后的 OAuth2 访问令牌转换为响应 DTO 并返回
        return success(BeanUtils.toBean(accessTokenDO, OAuth2AccessTokenRespDTO.class));
    }

}
