package org.nstep.engine.module.system.controller.admin.captcha;

import cn.hutool.core.util.StrUtil;
import com.xingyuv.captcha.model.common.ResponseModel;
import com.xingyuv.captcha.model.vo.CaptchaVO;
import com.xingyuv.captcha.service.CaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理后台 - 验证码控制器
 * <p>
 * 该类处理验证码相关的请求，包括生成验证码和校验验证码的功能。
 * 所有请求都通过不同的 API 端点进行管理。
 */
@Tag(name = "管理后台 - 验证码")
@RestController("adminCaptchaController")
@RequestMapping("/system/captcha")
public class CaptchaController {

    // 依赖注入验证码服务
    @Resource
    private CaptchaService captchaService;

    /**
     * 获取客户端的远程标识符
     * <p>
     * 通过客户端的 IP 地址和 User-Agent 生成一个唯一的标识符，用于验证码的生成和校验。
     *
     * @param request HttpServletRequest 请求对象
     * @return 客户端的唯一标识符
     */
    public static String getRemoteId(HttpServletRequest request) {
        String ip = ServletUtils.getClientIP(request);  // 获取客户端 IP 地址
        String ua = request.getHeader("user-agent");    // 获取客户端的 User-Agent
        if (StrUtil.isNotBlank(ip)) {
            return ip + ua;  // 如果 IP 地址不为空，返回 IP + User-Agent 作为标识符
        }
        return request.getRemoteAddr() + ua;  // 否则返回远程地址 + User-Agent
    }

    /**
     * 获取验证码
     * <p>
     * 生成验证码并返回给客户端。
     *
     * @param data    CaptchaVO 包含验证码请求的相关数据
     * @param request HttpServletRequest 请求对象
     * @return 返回验证码生成的响应模型
     */
    @PostMapping({"/get"})
    @Operation(summary = "获得验证码")
    @PermitAll
    public ResponseModel get(@RequestBody CaptchaVO data, HttpServletRequest request) {
        assert request.getRemoteHost() != null;  // 确保请求的远程主机信息不为空
        data.setBrowserInfo(getRemoteId(request));  // 设置验证码请求的浏览器信息
        return captchaService.get(data);  // 调用验证码服务生成验证码并返回
    }

    /**
     * 校验验证码
     * <p>
     * 校验客户端提交的验证码是否正确。
     *
     * @param data    CaptchaVO 包含验证码校验的相关数据
     * @param request HttpServletRequest 请求对象
     * @return 返回验证码校验的响应模型
     */
    @PostMapping("/check")
    @Operation(summary = "校验验证码")
    @PermitAll
    public ResponseModel check(@RequestBody CaptchaVO data, HttpServletRequest request) {
        data.setBrowserInfo(getRemoteId(request));  // 设置验证码校验的浏览器信息
        return captchaService.check(data);  // 调用验证码服务进行校验并返回结果
    }

}
