package org.nstep.engine.module.system.api.social;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.system.api.social.dto.*;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 社交应用的 RPC 服务接口
 * <p>
 * 该接口提供了与社交平台交互的相关 API，包括获取授权 URL、创建签名、获取手机信息、生成二维码、获取订阅模板、发送订阅消息等功能。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 社交应用")
public interface SocialClientApi {

    // 定义请求的前缀
    String PREFIX = ApiConstants.PREFIX + "/social-client";

    /**
     * 获取社交平台的授权 URL
     * <p>
     * 用于获取授权 URL，用户可以通过该 URL 进行授权操作。
     *
     * @param socialType  社交平台类型
     * @param userType    用户类型
     * @param redirectUri 重定向 URL
     * @return 返回授权 URL
     */
    @GetMapping(PREFIX + "/get-authorize-url")
    @Operation(summary = "获得社交平台的授权 URL")
    @Parameters({
            @Parameter(name = "socialType", description = "社交平台的类型", example = "1", required = true),
            @Parameter(name = "userType", description = "用户类型", example = "1", required = true),
            @Parameter(name = "redirectUri", description = "重定向 URL", example = "https://www.nstep.cn", required = true)
    })
    CommonResult<String> getAuthorizeUrl(@RequestParam("socialType") Integer socialType,
                                         @RequestParam("userType") Integer userType,
                                         @RequestParam("redirectUri") String redirectUri);

    /**
     * 创建微信公众号 JS SDK 初始化所需的签名
     * <p>
     * 用于生成微信公众号 JS SDK 初始化所需的签名，以便在客户端使用。
     *
     * @param userType 用户类型
     * @param url      访问的 URL
     * @return 返回签名信息
     */
    @GetMapping(PREFIX + "/create-wx-mp-jsapi-signature")
    @Operation(summary = "创建微信公众号 JS SDK 初始化所需的签名")
    @Parameters({
            @Parameter(name = "userType", description = "用户类型", example = "1", required = true),
            @Parameter(name = "url", description = "访问 URL", example = "https://www.nstep.cn", required = true)
    })
    CommonResult<SocialWxJsapiSignatureRespDTO> createWxMpJsapiSignature(@RequestParam("userType") Integer userType,
                                                                         @RequestParam("url") String url);

    /**
     * 获取微信小程序的手机信息
     * <p>
     * 通过授权码获取微信小程序用户的手机信息。
     *
     * @param userType  用户类型
     * @param phoneCode 手机授权码
     * @return 返回微信小程序的手机信息
     */
    @GetMapping(PREFIX + "/create-wx-ma-phone-number-info")
    @Operation(summary = "获得微信小程序的手机信息")
    @Parameters({
            @Parameter(name = "userType", description = "用户类型", example = "1", required = true),
            @Parameter(name = "phoneCode", description = "手机授权码", example = "engine11", required = true)
    })
    CommonResult<SocialWxPhoneNumberInfoRespDTO> getWxMaPhoneNumberInfo(@RequestParam("userType") Integer userType,
                                                                        @RequestParam("phoneCode") String phoneCode);

    /**
     * 获取微信小程序二维码
     * <p>
     * 用于生成小程序二维码。
     *
     * @param reqVO 请求参数
     * @return 返回二维码的字节数据
     */
    @GetMapping(PREFIX + "/get-wxa-qrcode")
    @Operation(summary = "获得小程序二维码")
    CommonResult<byte[]> getWxaQrcode(@SpringQueryMap SocialWxQrcodeReqDTO reqVO);

    /**
     * 获取微信小程序订阅模板列表
     * <p>
     * 用于获取微信小程序的订阅模板列表。
     *
     * @param userType 用户类型
     * @return 返回订阅模板列表
     */
    @GetMapping(PREFIX + "/get-wxa-subscribe-template-list")
    @Operation(summary = "获得微信小程订阅模板")
    CommonResult<List<SocialWxaSubscribeTemplateRespDTO>> getWxaSubscribeTemplateList(@RequestParam("userType") Integer userType);

    /**
     * 发送微信小程序订阅消息
     * <p>
     * 用于向微信小程序用户发送订阅消息。
     *
     * @param reqDTO 请求参数
     * @return 返回是否发送成功
     */
    @PostMapping(PREFIX + "/send-wxa-subscribe-message")
    @Operation(summary = "发送微信小程序订阅消息")
    CommonResult<Boolean> sendWxaSubscribeMessage(@Valid @RequestBody SocialWxaSubscribeMessageSendReqDTO reqDTO);

}
