package org.nstep.engine.module.system.api.social.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * 获取小程序码的请求 DTO
 * <p>
 * 该类用于生成微信小程序码的请求数据传输对象。通过此 DTO 可以向微信 API 发送请求，生成小程序码。
 * 详细文档参考：<a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/qrcode-link/qr-code/getUnlimitedQRCode.html">获取不限制的小程序码</a>
 */
@Schema(description = "RPC 服务 - 获得获取小程序码 Request DTO")
@Data
public class SocialWxQrcodeReqDTO {

    /**
     * 页面路径不能携带参数（参数请放在scene字段里）
     * <p>
     * 该常量用于说明生成二维码时，页面路径不能包含参数，参数应通过 `scene` 字段传递。
     */
    public static final String SCENE = "";

    /**
     * 二维码宽度
     * <p>
     * 默认值为 430，表示生成的小程序码的宽度。
     */
    public static final Integer WIDTH = 430;

    /**
     * 自动配置线条颜色，如果颜色依然是黑色，则说明不建议配置主色调
     * <p>
     * 默认值为 `true`，表示微信会自动为二维码配置线条颜色。
     */
    public static final Boolean AUTO_COLOR = true;

    /**
     * 检查页面路径是否存在
     * <p>
     * 默认值为 `true`，表示微信会检查页面路径是否有效。
     */
    public static final Boolean CHECK_PATH = true;

    /**
     * 是否需要透明底色
     * <p>
     * 默认值为 `true`，表示生成的小程序码将具有透明底色。
     * <p>
     * 如果 `hyaline` 为 `true`，生成的小程序码将没有白色背景，适用于需要透明背景的场景。
     */
    public static final Boolean HYALINE = true;

    /**
     * 场景值，用于生成二维码时传递的场景标识
     * <p>
     * 该字段用于指定二维码的场景，通常用于标识不同的业务场景。
     */
    @Schema(description = "场景", requiredMode = Schema.RequiredMode.REQUIRED, example = "1001")
    @NotEmpty(message = "场景不能为空")
    private String scene;

    /**
     * 页面路径，用于指定生成二维码后跳转的页面路径
     * <p>
     * 页面路径不能携带参数，参数请放在 `scene` 字段里。
     */
    @Schema(description = "页面路径", requiredMode = Schema.RequiredMode.REQUIRED, example = "pages/goods/index")
    @NotEmpty(message = "页面路径不能为空")
    private String path;

    /**
     * 二维码宽度
     * <p>
     * 可选字段，表示生成二维码的宽度，默认值为 430。
     */
    @Schema(description = "二维码宽度", example = "430")
    private Integer width;

    /**
     * 是否自动配置二维码的线条颜色
     * <p>
     * 可选字段，表示是否让微信自动配置二维码的线条颜色，默认值为 `true`。
     */
    @Schema(description = "是否需要透明底色", example = "true")
    private Boolean autoColor;

    /**
     * 是否检查页面路径是否存在
     * <p>
     * 可选字段，表示是否检查页面路径的有效性，默认值为 `true`。
     */
    @Schema(description = "是否检查 page 是否存在", example = "true")
    private Boolean checkPath;

    /**
     * 是否需要透明底色
     * <p>
     * 可选字段，表示生成的小程序码是否需要透明底色，默认值为 `true`。
     */
    @Schema(description = "是否需要透明底色", example = "true")
    private Boolean hyaline;

}
