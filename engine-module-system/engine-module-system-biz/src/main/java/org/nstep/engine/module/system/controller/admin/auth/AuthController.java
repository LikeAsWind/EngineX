package org.nstep.engine.module.system.controller.admin.auth;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.enums.CommonStatusEnum;
import org.nstep.engine.framework.common.enums.UserTypeEnum;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.security.config.SecurityProperties;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.module.system.controller.admin.auth.vo.*;
import org.nstep.engine.module.system.convert.auth.AuthConvert;
import org.nstep.engine.module.system.dal.dataobject.permission.MenuDO;
import org.nstep.engine.module.system.dal.dataobject.permission.RoleDO;
import org.nstep.engine.module.system.dal.dataobject.user.AdminUserDO;
import org.nstep.engine.module.system.enums.logger.LoginLogTypeEnum;
import org.nstep.engine.module.system.service.auth.AdminAuthService;
import org.nstep.engine.module.system.service.permission.MenuService;
import org.nstep.engine.module.system.service.permission.PermissionService;
import org.nstep.engine.module.system.service.permission.RoleService;
import org.nstep.engine.module.system.service.social.SocialClientService;
import org.nstep.engine.module.system.service.user.AdminUserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;
import static org.nstep.engine.framework.common.util.collection.CollectionUtils.convertSet;
import static org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

/**
 * 管理后台 - 认证控制器
 * <p>
 * 该类处理与认证相关的请求，包括登录、登出、注册、刷新令牌、获取权限信息、短信登录、社交登录等功能。
 * 所有请求都通过不同的 API 端点进行管理。
 */
@Tag(name = "管理后台 - 认证")
@RestController
@RequestMapping("/system/auth")
@Validated
@Slf4j
public class AuthController {

    // 依赖注入服务
    @Resource
    private AdminAuthService authService;
    @Resource
    private AdminUserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private MenuService menuService;
    @Resource
    private PermissionService permissionService;
    @Resource
    private SocialClientService socialClientService;
    @Resource
    private SecurityProperties securityProperties;

    /**
     * 账号密码登录
     * <p>
     * 使用账号和密码进行登录，返回登录成功后的用户信息。
     */
    @PostMapping("/login")
    @PermitAll
    @Operation(summary = "使用账号密码登录")
    public CommonResult<AuthLoginRespVO> login(@RequestBody @Valid AuthLoginReqVO reqVO) {
        return success(authService.login(reqVO));
    }

    /**
     * 登出系统
     * <p>
     * 用户登出，清除会话信息，并记录登出日志。
     */
    @PostMapping("/logout")
    @PermitAll
    @Operation(summary = "登出系统")
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        String token = SecurityFrameworkUtils.obtainAuthorization(request,
                securityProperties.getTokenHeader(), securityProperties.getTokenParameter());
        if (StrUtil.isNotBlank(token)) {
            authService.logout(token, LoginLogTypeEnum.LOGOUT_SELF.getType());
        }
        return success(true);
    }

    /**
     * 刷新令牌
     * <p>
     * 使用刷新令牌刷新用户的访问令牌，确保会话的持续有效。
     */
    @PostMapping("/refresh-token")
    @PermitAll
    @Operation(summary = "刷新令牌")
    @Parameter(name = "refreshToken", description = "刷新令牌", required = true)
    public CommonResult<AuthLoginRespVO> refreshToken(@RequestParam("refreshToken") String refreshToken) {
        return success(authService.refreshToken(refreshToken));
    }

    /**
     * 获取登录用户的权限信息
     * <p>
     * 获取当前登录用户的角色、权限、菜单等信息，并将其封装为响应对象。
     */
    @GetMapping("/get-permission-info")
    @Operation(summary = "获取登录用户的权限信息")
    public CommonResult<AuthPermissionInfoRespVO> getPermissionInfo() {
        // 1.1 获得用户信息
        AdminUserDO user = userService.getUser(getLoginUserId());
        if (user == null) {
            return success(null);
        }

        // 1.2 获得角色列表
        Set<Long> roleIds = permissionService.getUserRoleIdListByUserId(getLoginUserId());
        if (CollUtil.isEmpty(roleIds)) {
            return success(AuthConvert.INSTANCE.convert(user, Collections.emptyList(), Collections.emptyList()));
        }
        List<RoleDO> roles = roleService.getRoleList(roleIds);
        roles.removeIf(role -> !CommonStatusEnum.ENABLE.getStatus().equals(role.getStatus())); // 移除禁用的角色

        // 1.3 获得菜单列表
        Set<Long> menuIds = permissionService.getRoleMenuListByRoleId(convertSet(roles, RoleDO::getId));
        List<MenuDO> menuList = menuService.getMenuList(menuIds);
        menuList = menuService.filterDisableMenus(menuList);

        // 2. 拼接结果返回
        return success(AuthConvert.INSTANCE.convert(user, roles, menuList));
    }

    /**
     * 用户注册
     * <p>
     * 处理用户注册请求，创建新用户并返回登录响应。
     */
    @PostMapping("/register")
    @PermitAll
    @Operation(summary = "注册用户")
    public CommonResult<AuthLoginRespVO> register(@RequestBody @Valid AuthRegisterReqVO registerReqVO) {
        return success(authService.register(registerReqVO));
    }

    // ========== 短信登录相关 ==========

    /**
     * 短信验证码登录
     * <p>
     * 使用短信验证码进行登录，返回登录成功后的用户信息。
     */
    @PostMapping("/sms-login")
    @PermitAll
    @Operation(summary = "使用短信验证码登录")
    public CommonResult<AuthLoginRespVO> smsLogin(@RequestBody @Valid AuthSmsLoginReqVO reqVO) {
        return success(authService.smsLogin(reqVO));
    }

    /**
     * 发送手机验证码
     * <p>
     * 发送用于登录的短信验证码。
     */
    @PostMapping("/send-sms-code")
    @PermitAll
    @Operation(summary = "发送手机验证码")
    public CommonResult<Boolean> sendLoginSmsCode(@RequestBody @Valid AuthSmsSendReqVO reqVO) {
        authService.sendSmsCode(reqVO);
        return success(true);
    }

    // ========== 社交登录相关 ==========

    /**
     * 社交授权的跳转
     * <p>
     * 获取社交平台的授权 URL，供用户跳转到社交平台进行授权。
     */
    @GetMapping("/social-auth-redirect")
    @PermitAll
    @Operation(summary = "社交授权的跳转")
    @Parameters({
            @Parameter(name = "type", description = "社交类型", required = true),
            @Parameter(name = "redirectUri", description = "回调路径")
    })
    public CommonResult<String> socialLogin(@RequestParam("type") Integer type,
                                            @RequestParam("redirectUri") String redirectUri) {
        return success(socialClientService.getAuthorizeUrl(
                type, UserTypeEnum.ADMIN.getValue(), redirectUri));
    }

    /**
     * 社交快捷登录，使用授权码
     * <p>
     * 用户通过社交平台授权后，使用授权码进行快捷登录。
     */
    @PostMapping("/social-login")
    @PermitAll
    @Operation(summary = "社交快捷登录，使用 code 授权码", description = "适合未登录的用户，但是社交账号已绑定用户")
    public CommonResult<AuthLoginRespVO> socialQuickLogin(@RequestBody @Valid AuthSocialLoginReqVO reqVO) {
        return success(authService.socialLogin(reqVO));
    }

}
