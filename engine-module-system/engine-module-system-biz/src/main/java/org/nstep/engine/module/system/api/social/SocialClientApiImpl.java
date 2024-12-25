package org.nstep.engine.module.system.api.social;

import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxJsapiSignature;
import me.chanjar.weixin.common.bean.subscribemsg.TemplateInfo;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.system.api.social.dto.*;
import org.nstep.engine.module.system.enums.social.SocialTypeEnum;
import org.nstep.engine.module.system.service.social.SocialClientService;
import org.nstep.engine.module.system.service.social.SocialUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.hutool.core.collection.CollUtil.findOne;
import static org.nstep.engine.framework.common.pojo.CommonResult.success;
import static org.nstep.engine.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 社交应用的 API 实现类
 * <p>
 * 该类提供了社交平台（如微信小程序等）相关的 RESTful API 接口，
 * 包括获取授权 URL、生成 JSAPI 签名、获取用户手机号信息、发送订阅消息等功能。
 * 提供给 Feign 调用，处理与社交平台相关的业务逻辑。
 */
@RestController
@Validated
@Slf4j
public class SocialClientApiImpl implements SocialClientApi {

    @Resource
    private SocialClientService socialClientService; // 社交客户端服务，处理与社交平台相关的业务逻辑
    @Resource
    private SocialUserService socialUserService; // 社交用户服务，处理社交用户相关操作

    /**
     * 获取授权 URL
     *
     * @param socialType  社交平台类型
     * @param userType    用户类型
     * @param redirectUri 授权成功后重定向的 URI
     * @return 授权 URL
     */
    @Override
    public CommonResult<String> getAuthorizeUrl(Integer socialType, Integer userType, String redirectUri) {
        return success(socialClientService.getAuthorizeUrl(socialType, userType, redirectUri));
    }

    /**
     * 创建微信小程序 JSAPI 签名
     *
     * @param userType 用户类型
     * @param url      请求的 URL
     * @return 微信 JSAPI 签名信息
     */
    @Override
    public CommonResult<SocialWxJsapiSignatureRespDTO> createWxMpJsapiSignature(Integer userType, String url) {
        WxJsapiSignature signature = socialClientService.createWxMpJsapiSignature(userType, url);
        return success(BeanUtils.toBean(signature, SocialWxJsapiSignatureRespDTO.class));
    }

    /**
     * 获取微信小程序用户手机号信息
     *
     * @param userType  用户类型
     * @param phoneCode 手机号的加密信息
     * @return 微信小程序用户手机号信息
     */
    @Override
    public CommonResult<SocialWxPhoneNumberInfoRespDTO> getWxMaPhoneNumberInfo(Integer userType, String phoneCode) {
        WxMaPhoneNumberInfo info = socialClientService.getWxMaPhoneNumberInfo(userType, phoneCode);
        return success(BeanUtils.toBean(info, SocialWxPhoneNumberInfoRespDTO.class));
    }

    /**
     * 获取微信小程序二维码
     *
     * @param reqVO 请求参数，包含二维码生成的相关信息
     * @return 生成的二维码字节数组
     */
    @Override
    public CommonResult<byte[]> getWxaQrcode(SocialWxQrcodeReqDTO reqVO) {
        return success(socialClientService.getWxaQrcode(reqVO));
    }

    /**
     * 获取微信小程序订阅模板列表
     *
     * @param userType 用户类型
     * @return 订阅模板列表
     */
    @Override
    public CommonResult<List<SocialWxaSubscribeTemplateRespDTO>> getWxaSubscribeTemplateList(Integer userType) {
        List<TemplateInfo> list = socialClientService.getSubscribeTemplateList(userType);
        return success(convertList(list, item -> {
            SocialWxaSubscribeTemplateRespDTO dto = BeanUtils.toBean(item, SocialWxaSubscribeTemplateRespDTO.class);
            dto.setId(item.getPriTmplId());
            return dto;
        }));
    }

    /**
     * 发送微信小程序订阅消息
     *
     * @param reqDTO 请求参数，包含订阅消息发送的相关信息
     * @return 发送结果
     */
    @Override
    public CommonResult<Boolean> sendWxaSubscribeMessage(SocialWxaSubscribeMessageSendReqDTO reqDTO) {
        // 1.1 获得订阅模版列表
        List<TemplateInfo> templateList = socialClientService.getSubscribeTemplateList(reqDTO.getUserType());
        if (CollUtil.isEmpty(templateList)) {
            log.warn("[sendSubscribeMessage][reqDTO({}) 发送订阅消息失败，原因：没有找到订阅模板]", reqDTO);
            return success(false);
        }
        // 1.2 获得需要使用的模版
        TemplateInfo template = findOne(templateList, item -> ObjUtil.equal(item.getTitle(), reqDTO.getTemplateTitle()));
        if (template == null) {
            log.warn("[sendWxaSubscribeMessage][reqDTO({}) 发送订阅消息失败，原因：没有找到订阅模板]", reqDTO);
            return success(false);
        }

        // 2. 获得社交用户
        SocialUserRespDTO socialUser = socialUserService.getSocialUserByUserId(reqDTO.getUserType(), reqDTO.getUserId(),
                SocialTypeEnum.WECHAT_MINI_APP.getType());
        if (StrUtil.isBlankIfStr(socialUser.getOpenid())) {
            log.warn("[sendWxaSubscribeMessage][reqDTO({}) 发送订阅消息失败，原因：会员 openid 缺失]", reqDTO);
            return success(false);
        }

        // 3. 发送订阅消息
        socialClientService.sendSubscribeMessage(reqDTO, template.getPriTmplId(), socialUser.getOpenid());
        return success(true);
    }

}
