package org.nstep.engine.framework.apilog.core.filter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.apilog.core.annotation.ApiAccessLog;
import org.nstep.engine.framework.apilog.core.enums.OperateTypeEnum;
import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.json.JsonUtils;
import org.nstep.engine.framework.common.util.monitor.TracerUtils;
import org.nstep.engine.framework.common.util.servlet.ServletUtils;
import org.nstep.engine.framework.web.config.WebProperties;
import org.nstep.engine.framework.web.core.filter.ApiRequestFilter;
import org.nstep.engine.framework.web.core.util.WebFrameworkUtils;
import org.nstep.engine.module.infra.api.logger.ApiAccessLogApi;
import org.nstep.engine.module.infra.api.logger.dto.ApiAccessLogCreateReqDTO;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.Map;

import static org.nstep.engine.framework.apilog.core.interceptor.ApiAccessLogInterceptor.ATTRIBUTE_HANDLER_METHOD;
import static org.nstep.engine.framework.common.util.json.JsonUtils.toJsonString;


/**
 * API 访问日志 Filter
 * <p>
 * 目的：记录 API 访问日志到数据库中
 */
@Slf4j
public class ApiAccessLogFilter extends ApiRequestFilter {

    // 定义需要脱敏的字段，如密码、token等
    private static final String[] SANITIZE_KEYS = new String[]{"password", "token", "accessToken", "refreshToken"};

    private final String applicationName;  // 应用名称
    private final ApiAccessLogApi apiAccessLogApi;  // 用于记录访问日志的 API 接口

    /**
     * 构造方法，初始化应用名称和日志 API 接口
     *
     * @param webProperties   Web 配置属性
     * @param applicationName 应用名称
     * @param apiAccessLogApi API 访问日志接口
     */
    public ApiAccessLogFilter(WebProperties webProperties, String applicationName, ApiAccessLogApi apiAccessLogApi) {
        super(webProperties);
        this.applicationName = applicationName;
        this.apiAccessLogApi = apiAccessLogApi;
    }

    /**
     * 根据 HTTP 请求方法解析操作日志类型
     *
     * @param request HTTP 请求
     * @return 操作类型枚举值
     */
    private static OperateTypeEnum parseOperateLogType(HttpServletRequest request) {
        RequestMethod requestMethod = RequestMethod.resolve(request.getMethod());
        if (requestMethod == null) {
            return OperateTypeEnum.OTHER;
        }
        return switch (requestMethod) {
            case GET -> OperateTypeEnum.GET;
            case POST -> OperateTypeEnum.CREATE;
            case PUT -> OperateTypeEnum.UPDATE;
            case DELETE -> OperateTypeEnum.DELETE;
            default -> OperateTypeEnum.OTHER;
        };
    }

    /**
     * 对请求参数进行脱敏处理，移除敏感字段
     *
     * @param map          请求参数的 Map
     * @param sanitizeKeys 需要脱敏的字段
     * @return 脱敏后的参数 JSON 字符串
     */
    private static String sanitizeMap(Map<String, ?> map, String[] sanitizeKeys) {
        if (CollUtil.isEmpty(map)) {
            return null;
        }
        // 移除指定的脱敏字段
        if (sanitizeKeys != null) {
            MapUtil.removeAny(map, sanitizeKeys);
        }
        MapUtil.removeAny(map, SANITIZE_KEYS);  // 默认脱敏字段
        return toJsonString(map);  // 转换为 JSON 字符串
    }

    /**
     * 对请求体的 JSON 字符串进行脱敏处理
     *
     * @param jsonString   请求体的 JSON 字符串
     * @param sanitizeKeys 需要脱敏的字段
     * @return 脱敏后的 JSON 字符串
     */
    private static String sanitizeJson(String jsonString, String[] sanitizeKeys) {
        if (StrUtil.isEmpty(jsonString)) {
            return null;
        }
        try {
            JsonNode rootNode = JsonUtils.parseTree(jsonString);  // 解析 JSON 字符串
            sanitizeJson(rootNode, sanitizeKeys);  // 递归脱敏
            return toJsonString(rootNode);  // 转换为 JSON 字符串
        } catch (Exception e) {
            // 脱敏失败时，记录日志但不影响请求
            log.error("[sanitizeJson][脱敏({}) 发生异常]", jsonString, e);
            return jsonString;  // 返回原始 JSON 字符串
        }
    }

    /**
     * 对 CommonResult 对象的 JSON 进行脱敏处理
     *
     * @param commonResult CommonResult 对象
     * @param sanitizeKeys 需要脱敏的字段
     * @return 脱敏后的 JSON 字符串
     */
    private static String sanitizeJson(CommonResult<?> commonResult, String[] sanitizeKeys) {
        if (commonResult == null) {
            return null;
        }
        String jsonString = toJsonString(commonResult);  // 转换为 JSON 字符串
        try {
            JsonNode rootNode = JsonUtils.parseTree(jsonString);
            sanitizeJson(rootNode.get("data"), sanitizeKeys);  // 只脱敏 data 字段
            return toJsonString(rootNode);  // 转换为 JSON 字符串
        } catch (Exception e) {
            log.error("[sanitizeJson][脱敏({}) 发生异常]", jsonString, e);
            return jsonString;  // 返回原始 JSON 字符串
        }
    }

    /**
     * 对 JSON 节点进行递归脱敏处理
     *
     * @param node         JSON 节点
     * @param sanitizeKeys 需要脱敏的字段
     */
    private static void sanitizeJson(JsonNode node, String[] sanitizeKeys) {
        // 情况一：数组，遍历处理
        if (node.isArray()) {
            for (JsonNode childNode : node) {
                sanitizeJson(childNode, sanitizeKeys);  // 递归处理数组元素
            }
            return;
        }
        // 情况二：非 Object 类型，直接返回
        if (!node.isObject()) {
            return;
        }
        // 情况三：Object 类型，遍历处理每个字段
        Iterator<Map.Entry<String, JsonNode>> iterator = node.fields();
        while (iterator.hasNext()) {
            Map.Entry<String, JsonNode> entry = iterator.next();
            // 如果字段是敏感字段，移除该字段
            if (ArrayUtil.contains(sanitizeKeys, entry.getKey()) || ArrayUtil.contains(SANITIZE_KEYS, entry.getKey())) {
                iterator.remove();
                continue;
            }
            sanitizeJson(entry.getValue(), sanitizeKeys);  // 递归处理字段值
        }
    }

    /**
     * 执行请求过滤，记录 API 访问日志
     *
     * @param request     HTTP 请求
     * @param response    HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    @SuppressWarnings("NullableProblems")
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 记录请求开始时间
        LocalDateTime beginTime = LocalDateTime.now();
        // 提前获取请求参数，避免 XssFilter 过滤
        Map<String, String> queryString = ServletUtils.getParamMap(request);
        String requestBody = ServletUtils.isJsonRequest(request) ? ServletUtils.getBody(request) : null;

        try {
            // 继续执行过滤器链
            filterChain.doFilter(request, response);
            // 请求成功后，记录访问日志
            createApiAccessLog(request, beginTime, queryString, requestBody, null);
        } catch (Exception ex) {
            // 异常发生时，记录访问日志
            createApiAccessLog(request, beginTime, queryString, requestBody, ex);
            throw ex;  // 抛出异常
        }
    }

    /**
     * 创建并记录 API 访问日志
     *
     * @param request     HTTP 请求
     * @param beginTime   请求开始时间
     * @param queryString 请求参数
     * @param requestBody 请求体
     * @param ex          异常信息
     */
    private void createApiAccessLog(HttpServletRequest request, LocalDateTime beginTime, Map<String, String> queryString, String requestBody, Exception ex) {
        ApiAccessLogCreateReqDTO accessLog = new ApiAccessLogCreateReqDTO();
        try {
            // 构建访问日志
            boolean enable = buildApiAccessLog(accessLog, request, beginTime, queryString, requestBody, ex);
            if (!enable) {
                return;  // 如果不需要记录日志，返回
            }
            // 异步保存访问日志
            apiAccessLogApi.createApiAccessLogAsync(accessLog);
        } catch (Throwable th) {
            log.error("[createApiAccessLog][url({}) log({}) 发生异常]", request.getRequestURI(), toJsonString(accessLog), th);
        }
    }

    /**
     * 构建 API 访问日志对象
     *
     * @param accessLog   访问日志对象
     * @param request     HTTP 请求
     * @param beginTime   请求开始时间
     * @param queryString 请求参数
     * @param requestBody 请求体
     * @param ex          异常信息
     * @return 是否成功构建日志
     */
    private boolean buildApiAccessLog(ApiAccessLogCreateReqDTO accessLog, HttpServletRequest request, LocalDateTime beginTime, Map<String, String> queryString, String requestBody, Exception ex) {
        // 判断是否需要记录操作日志
        HandlerMethod handlerMethod = (HandlerMethod) request.getAttribute(ATTRIBUTE_HANDLER_METHOD);
        ApiAccessLog accessLogAnnotation = null;
        if (handlerMethod != null) {
            accessLogAnnotation = handlerMethod.getMethodAnnotation(ApiAccessLog.class);
            if (accessLogAnnotation != null && BooleanUtil.isFalse(accessLogAnnotation.enable())) {
                return false;  // 如果注解禁止记录日志，返回 false
            }
        }

        // 设置用户信息
        accessLog.setUserId(WebFrameworkUtils.getLoginUserId(request));
        accessLog.setUserType(WebFrameworkUtils.getLoginUserType(request));
        // 设置访问结果
        CommonResult<?> result = WebFrameworkUtils.getCommonResult(request);
        if (result != null) {
            accessLog.setResultCode(result.getCode());
            accessLog.setResultMsg(result.getMsg());
        } else if (ex != null) {
            accessLog.setResultCode(GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR.getCode());
            accessLog.setResultMsg(ExceptionUtil.getRootCauseMessage(ex));
        } else {
            accessLog.setResultCode(GlobalErrorCodeConstants.SUCCESS.getCode());
            accessLog.setResultMsg("");
        }
        // 设置请求字段
        accessLog.setTraceId(TracerUtils.getTraceId());
        accessLog.setApplicationName(applicationName);
        accessLog.setRequestUrl(request.getRequestURI());
        accessLog.setRequestMethod(request.getMethod());
        accessLog.setUserAgent(ServletUtils.getUserAgent(request));
        accessLog.setUserIp(ServletUtils.getClientIP(request));
        String[] sanitizeKeys = accessLogAnnotation != null ? accessLogAnnotation.sanitizeKeys() : null;
        Boolean requestEnable = accessLogAnnotation != null ? accessLogAnnotation.requestEnable() : Boolean.TRUE;
        if (!BooleanUtil.isFalse(requestEnable)) {  // 默认记录请求日志
            Map<String, Object> requestParams = MapUtil.<String, Object>builder()
                    .put("query", sanitizeMap(queryString, sanitizeKeys))
                    .put("body", sanitizeJson(requestBody, sanitizeKeys))
                    .build();
            accessLog.setRequestParams(toJsonString(requestParams));
        }
        // 设置响应字段
        Boolean responseEnable = accessLogAnnotation != null ? accessLogAnnotation.responseEnable() : Boolean.FALSE;
        if (BooleanUtil.isTrue(responseEnable)) {  // 默认不记录响应日志
            accessLog.setResponseBody(sanitizeJson(result, sanitizeKeys));
        }
        // 记录请求处理时长
        accessLog.setBeginTime(beginTime);
        accessLog.setEndTime(LocalDateTime.now());
        accessLog.setDuration((int) LocalDateTimeUtil.between(accessLog.getBeginTime(), accessLog.getEndTime(), ChronoUnit.MILLIS));

        // 设置操作模块、操作名称和操作类型
        if (handlerMethod != null) {
            Tag tagAnnotation = handlerMethod.getBeanType().getAnnotation(Tag.class);
            Operation operationAnnotation = handlerMethod.getMethodAnnotation(Operation.class);
            String operateModule = accessLogAnnotation != null ? accessLogAnnotation.operateModule() : tagAnnotation != null ? StrUtil.nullToDefault(tagAnnotation.name(), tagAnnotation.description()) : null;
            String operateName = accessLogAnnotation != null ? accessLogAnnotation.operateName() : operationAnnotation != null ? operationAnnotation.summary() : null;
            OperateTypeEnum operateType = accessLogAnnotation != null && accessLogAnnotation.operateType().length > 0 ? accessLogAnnotation.operateType()[0] : parseOperateLogType(request);
            accessLog.setOperateModule(operateModule);
            accessLog.setOperateName(operateName);
            accessLog.setOperateType(operateType.getType());
        }
        return true;
    }

}
