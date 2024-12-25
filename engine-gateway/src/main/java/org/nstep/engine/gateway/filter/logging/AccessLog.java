package org.nstep.engine.gateway.filter.logging;

import lombok.Data;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;

import java.time.LocalDateTime;

/**
 * 网关的访问日志
 * <p>
 * 该类用于记录网关的访问日志信息，包括请求的各个方面，如链路追踪编号、用户信息、请求和响应的内容等。
 */
@Data
public class AccessLog {

    /**
     * 链路追踪编号
     * <p>
     * 用于追踪请求在分布式系统中的路径，通常用于日志分析和故障排查。
     */
    private String traceId;

    /**
     * 用户编号
     * <p>
     * 用于标识发起请求的用户，通常为用户的唯一标识符。
     */
    private Long userId;

    /**
     * 用户类型
     * <p>
     * 用于标识用户的类型（例如，普通用户、管理员等）。
     */
    private Integer userType;

    /**
     * 路由
     * <p>
     * 路由信息类似于 ApiAccessLogCreateReqDTO 中的 applicationName，用于标识请求访问的服务或资源。
     */
    private Route route;

    /**
     * 协议
     * <p>
     * 请求的协议类型，例如 http 或 https。
     */
    private String schema;

    /**
     * 请求方法名
     * <p>
     * HTTP 请求的方法类型，例如 GET、POST、PUT、DELETE 等。
     */
    private String requestMethod;

    /**
     * 访问地址
     * <p>
     * 请求的 URL 地址。
     */
    private String requestUrl;

    /**
     * 查询参数
     * <p>
     * 请求 URL 中的查询参数，以键值对形式存储。
     */
    private MultiValueMap<String, String> queryParams;

    /**
     * 请求体
     * <p>
     * 请求的 body 内容，通常用于 POST 或 PUT 请求。
     */
    private String requestBody;

    /**
     * 请求头
     * <p>
     * 请求中的头部信息，以键值对形式存储。
     */
    private MultiValueMap<String, String> requestHeaders;

    /**
     * 用户 IP
     * <p>
     * 发起请求的用户的 IP 地址。
     */
    private String userIp;

    /**
     * 响应体
     * <p>
     * 响应的 body 内容，类似于 ApiAccessLogCreateReqDTO 中的 resultCode + resultMsg。
     */
    private String responseBody;

    /**
     * 响应头
     * <p>
     * 响应中的头部信息，以键值对形式存储。
     */
    private MultiValueMap<String, String> responseHeaders;

    /**
     * 响应结果
     * <p>
     * HTTP 响应的状态码，例如 200、404、500 等。
     */
    private HttpStatus httpStatus;

    /**
     * 开始请求时间
     * <p>
     * 请求开始的时间，通常用于计算请求的执行时长。
     */
    private LocalDateTime startTime;

    /**
     * 结束请求时间
     * <p>
     * 请求结束的时间，通常用于计算请求的执行时长。
     */
    private LocalDateTime endTime;

    /**
     * 执行时长，单位：毫秒
     * <p>
     * 记录请求从开始到结束的执行时长，单位为毫秒。
     */
    private Integer duration;

}
