package org.nstep.engine.gateway.handler;

import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.gateway.util.WebFrameworkUtils;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants.INTERNAL_SERVER_ERROR;

/**
 * Gateway 的全局异常处理器，将异常翻译成 CommonResult + 对应的异常编号
 * <p>
 * 本类用于处理 Spring Cloud Gateway 中的异常，将捕获到的异常转换为统一的响应格式（CommonResult）。
 * 该处理器与 engine-spring-boot-starter-web 中的 GlobalExceptionHandler 类功能一致。
 * <p>
 * 通过实现 {@link ErrorWebExceptionHandler} 接口，提供统一的异常处理机制，避免重复的异常处理代码。
 */
@Component
@Order(-1) // 设置该异常处理器的优先级高于默认的 Spring Cloud Gateway 的 ErrorWebExceptionHandler 实现
@Slf4j // 使用 Lombok 提供的日志功能
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    /**
     * 处理异常并返回给客户端
     * <p>
     * 如果响应已经提交，则直接返回异常；否则，根据异常类型将其转换为 CommonResult 格式，并返回给客户端。
     *
     * @param exchange 当前请求的交换对象
     * @param ex       捕获到的异常
     * @return Mono<Void> 异步响应
     */
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        // 如果响应已经提交，直接返回异常
        ServerHttpResponse response = exchange.getResponse();
        if (response.isCommitted()) {
            return Mono.error(ex);
        }

        // 根据异常类型进行处理
        CommonResult<?> result;
        if (ex instanceof ResponseStatusException) {
            // 如果是 ResponseStatusException 类型的异常，调用对应的处理方法
            result = responseStatusExceptionHandler(exchange, (ResponseStatusException) ex);
        } else {
            // 其他异常类型，调用默认的异常处理方法
            result = defaultExceptionHandler(exchange, ex);
        }

        // 将处理结果写入响应体并返回给客户端
        return WebFrameworkUtils.writeJSON(exchange, result);
    }

    /**
     * 处理 Spring Cloud Gateway 默认抛出的 ResponseStatusException 异常
     * <p>
     * 对于 ResponseStatusException 异常类型，进行日志记录并将异常信息转化为 CommonResult 格式。
     *
     * @param exchange 当前请求的交换对象
     * @param ex       捕获到的 ResponseStatusException 异常
     * @return CommonResult<?> 转换后的统一响应格式
     */
    private CommonResult<?> responseStatusExceptionHandler(ServerWebExchange exchange,
                                                           ResponseStatusException ex) {
        // TODO 需要根据业务需求进一步精细化异常信息的翻译，避免返回给用户的错误信息过于模糊
        ServerHttpRequest request = exchange.getRequest();
        // 记录错误日志
        log.error("[responseStatusExceptionHandler][uri({}/{}) 发生异常]", request.getURI(), request.getMethod(), ex);
        // 返回错误的 CommonResult
        return CommonResult.error(ex.getStatusCode().value(), ex.getReason());
    }

    /**
     * 处理系统异常，作为兜底处理所有未处理的异常
     * <p>
     * 对于系统异常，记录日志并返回一个通用的错误响应。
     *
     * @param exchange 当前请求的交换对象
     * @param ex       捕获到的异常
     * @return CommonResult<?> 转换后的统一响应格式
     */
    @ExceptionHandler(value = Exception.class)
    public CommonResult<?> defaultExceptionHandler(ServerWebExchange exchange,
                                                   Throwable ex) {
        ServerHttpRequest request = exchange.getRequest();
        // 记录错误日志
        log.error("[defaultExceptionHandler][uri({}/{}) 发生异常]", request.getURI(), request.getMethod(), ex);
        // TODO 可以根据需求决定是否插入异常日志到数据库或其他日志存储系统
        // 返回通用的错误响应
        return CommonResult.error(INTERNAL_SERVER_ERROR.getCode(), INTERNAL_SERVER_ERROR.getMsg());
    }

}
