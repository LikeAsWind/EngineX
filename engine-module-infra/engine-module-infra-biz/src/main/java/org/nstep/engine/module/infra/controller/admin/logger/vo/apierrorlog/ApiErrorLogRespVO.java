package org.nstep.engine.module.infra.controller.admin.logger.vo.apierrorlog;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.nstep.engine.framework.excel.core.annotations.DictFormat;
import org.nstep.engine.framework.excel.core.convert.DictConvert;
import org.nstep.engine.module.infra.enums.DictTypeConstants;

import java.time.LocalDateTime;

/**
 * API 错误日志响应数据传输对象（Response VO）
 * 用于管理后台展示 API 错误日志的详细信息。
 */
@Schema(description = "管理后台 - API 错误日志 Response VO")
@Data
@ExcelIgnoreUnannotated // 忽略未标注 @ExcelProperty 注解的字段
public class ApiErrorLogRespVO {

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @ExcelProperty("编号")
    private Integer id; // 错误日志的唯一标识符

    @Schema(description = "链路追踪编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "66600cb6-7852-11eb-9439-0242ac130002")
    @ExcelProperty("链路追踪编号")
    private String traceId; // 追踪此请求的唯一链路编号

    @Schema(description = "用户编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "666")
    @ExcelProperty("用户编号")
    private Integer userId; // 用户的唯一标识符

    @Schema(description = "用户类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "用户类型", converter = DictConvert.class)
    @DictFormat(org.nstep.engine.module.system.enums.DictTypeConstants.USER_TYPE)
    private Integer userType; // 用户类型（如管理员、普通用户等）

    @Schema(description = "应用名", requiredMode = Schema.RequiredMode.REQUIRED, example = "dashboard")
    @ExcelProperty("应用名")
    private String applicationName; // 出现错误的应用名称

    @Schema(description = "请求方法名", requiredMode = Schema.RequiredMode.REQUIRED, example = "GET")
    @ExcelProperty("请求方法名")
    private String requestMethod; // 请求的 HTTP 方法（如 GET、POST）

    @Schema(description = "请求地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "/xx/yy")
    @ExcelProperty("请求地址")
    private String requestUrl; // 请求的 URL 地址

    @Schema(description = "请求参数", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("请求参数")
    private String requestParams; // 请求时传递的参数

    @Schema(description = "用户 IP", requiredMode = Schema.RequiredMode.REQUIRED, example = "127.0.0.1")
    @ExcelProperty("用户 IP")
    private String userIp; // 用户的 IP 地址

    @Schema(description = "浏览器 UA", requiredMode = Schema.RequiredMode.REQUIRED, example = "Mozilla/5.0")
    @ExcelProperty("浏览器 UA")
    private String userAgent; // 用户的浏览器 User-Agent 字符串

    @Schema(description = "异常发生时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生时间")
    private LocalDateTime exceptionTime; // 异常发生的具体时间

    @Schema(description = "异常名", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常名")
    private String exceptionName; // 异常的类名

    @Schema(description = "异常导致的消息", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常导致的消息")
    private String exceptionMessage; // 异常的具体错误消息

    @Schema(description = "异常导致的根消息", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常导致的根消息")
    private String exceptionRootCauseMessage; // 异常的根本原因消息

    @Schema(description = "异常的栈轨迹", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常的栈轨迹")
    private String exceptionStackTrace; // 异常的栈追踪信息

    @Schema(description = "异常发生的类全名", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生的类全名")
    private String exceptionClassName; // 异常发生的类的全名（包括包名）

    @Schema(description = "异常发生的类文件", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生的类文件")
    private String exceptionFileName; // 异常发生的类文件名

    @Schema(description = "异常发生的方法名", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生的方法名")
    private String exceptionMethodName; // 异常发生的方法名

    @Schema(description = "异常发生的方法所在行", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("异常发生的方法所在行")
    private Integer exceptionLineNumber; // 异常发生的方法的行号

    @Schema(description = "处理状态", requiredMode = Schema.RequiredMode.REQUIRED, example = "0")
    @ExcelProperty(value = "处理状态", converter = DictConvert.class)
    @DictFormat(DictTypeConstants.API_ERROR_LOG_PROCESS_STATUS)
    private Integer processStatus; // 错误日志的处理状态（如已处理、未处理等）

    @Schema(description = "处理时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("处理时间")
    private LocalDateTime processTime; // 错误日志处理的时间

    @Schema(description = "处理用户编号", example = "233")
    @ExcelProperty("处理用户编号")
    private Integer processUserId; // 处理此错误日志的用户编号

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime; // 错误日志的创建时间

}
