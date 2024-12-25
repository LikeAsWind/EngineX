package org.nstep.engine.module.infra.controller.admin.logger.vo.apiaccesslog;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.nstep.engine.framework.excel.core.annotations.DictFormat;
import org.nstep.engine.framework.excel.core.convert.DictConvert;
import org.nstep.engine.module.system.enums.DictTypeConstants;

import java.time.LocalDateTime;

/**
 * 管理后台 - API 访问日志响应 VO
 * <p>
 * 该类用于封装 API 访问日志的响应数据。
 * </p>
 */
@Schema(description = "管理后台 - API 访问日志 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ApiAccessLogRespVO {

    /**
     * 日志主键
     * <p>
     * 唯一标识每一条日志记录。
     * </p>
     */
    @Schema(description = "日志主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("日志主键")
    private Long id;

    /**
     * 链路追踪编号
     * <p>
     * 用于标识一次请求的全程，便于追踪请求链路。
     * </p>
     */
    @Schema(description = "链路追踪编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "66600cb6-7852-11eb-9439-0242ac130002")
    @ExcelProperty("链路追踪编号")
    private String traceId;

    /**
     * 用户编号
     * <p>
     * 表示执行该操作的用户编号。
     * </p>
     */
    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    @ExcelProperty("用户编号")
    private Long userId;

    /**
     * 用户类型
     * <p>
     * 表示用户类型，参考 `UserTypeEnum` 枚举。
     * </p>
     */
    @Schema(description = "用户类型，参见 UserTypeEnum 枚举", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "用户类型", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.USER_TYPE)
    private Integer userType;

    /**
     * 应用名
     * <p>
     * 表示发起请求的应用名。
     * </p>
     */
    @Schema(description = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard")
    @ExcelProperty("应用名")
    private String applicationName;

    /**
     * 请求方法名
     * <p>
     * 表示 HTTP 请求方法，例如 GET、POST。
     * </p>
     */
    @Schema(description = "请求方法名", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @ExcelProperty("请求方法名")
    private String requestMethod;

    /**
     * 请求地址
     * <p>
     * 表示请求的 URL 地址。
     * </p>
     */
    @Schema(description = "请求地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xxx/yyy")
    @ExcelProperty("请求地址")
    private String requestUrl;

    /**
     * 请求参数
     * <p>
     * 表示请求中携带的参数。
     * </p>
     */
    @Schema(description = "请求参数")
    @ExcelProperty("请求参数")
    private String requestParams;

    /**
     * 响应结果
     * <p>
     * 表示 API 请求的响应结果内容。
     * </p>
     */
    @Schema(description = "响应结果")
    @ExcelProperty("响应结果")
    private String responseBody;

    /**
     * 用户 IP
     * <p>
     * 表示发起请求的用户的 IP 地址。
     * </p>
     */
    @Schema(description = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @ExcelProperty("用户 IP")
    private String userIp;

    /**
     * 浏览器 UA
     * <p>
     * 表示发起请求的浏览器的 User-Agent 字符串。
     * </p>
     */
    @Schema(description = "浏览器 UA", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    @ExcelProperty("浏览器 UA")
    private String userAgent;

    /**
     * 操作模块
     * <p>
     * 表示执行操作的模块名称。
     * </p>
     */
    @Schema(description = "操作模块", requiredMode = Schema.RequiredMode.REQUIRED, example = "商品模块")
    @ExcelProperty("操作模块")
    private String operateModule;

    /**
     * 操作名
     * <p>
     * 表示执行的具体操作名称。
     * </p>
     */
    @Schema(description = "操作名", requiredMode = Schema.RequiredMode.REQUIRED, example = "创建商品")
    @ExcelProperty("操作名")
    private String operateName;

    /**
     * 操作分类
     * <p>
     * 表示操作的类型，参考 `OPERATE_TYPE` 枚举。
     * </p>
     */
    @Schema(description = "操作分类", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "操作分类", converter = DictConvert.class)
    @DictFormat(org.nstep.engine.module.infra.enums.DictTypeConstants.OPERATE_TYPE)
    private Integer operateType;

    /**
     * 开始请求时间
     * <p>
     * 表示请求开始的时间。
     * </p>
     */
    @Schema(description = "开始请求时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("开始请求时间")
    private LocalDateTime beginTime;

    /**
     * 结束请求时间
     * <p>
     * 表示请求结束的时间。
     * </p>
     */
    @Schema(description = "结束请求时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("结束请求时间")
    private LocalDateTime endTime;

    /**
     * 执行时长
     * <p>
     * 表示请求处理的时长，单位为毫秒。
     * </p>
     */
    @Schema(description = "执行时长", requiredMode = Schema.RequiredMode.REQUIRED, example = "100")
    @ExcelProperty("执行时长")
    private Integer duration;

    /**
     * 结果码
     * <p>
     * 表示请求的结果码，0 表示成功，非 0 表示失败。
     * </p>
     */
    @Schema(description = "结果码", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty("结果码")
    private Integer resultCode;

    /**
     * 结果提示
     * <p>
     * 表示请求的结果提示信息，通常是错误或成功的描述。
     * </p>
     */
    @Schema(description = "结果提示", example = "engine源码，牛逼！")
    @ExcelProperty("结果提示")
    private String resultMsg;

    /**
     * 创建时间
     * <p>
     * 表示该日志记录的创建时间。
     * </p>
     */
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createTime;

}
