package org.nstep.engine.framework.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nstep.engine.framework.common.exception.enums.ServiceErrorCodeRange;

/**
 * 业务逻辑异常 Exception
 * 用于表示在业务逻辑处理过程中发生的异常情况
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ServiceException extends RuntimeException {

    /**
     * 业务错误码
     * 用于标识不同类型的业务逻辑错误
     *
     * @see ServiceErrorCodeRange
     */
    @Getter
    private Integer code;
    /**
     * 错误提示
     * 用于提供给用户或开发者的错误信息
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    /**
     * 构造方法，根据错误码枚举创建异常
     *
     * @param errorCode 错误码枚举
     */
    public ServiceException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    /**
     * 构造方法，根据错误码和错误提示创建异常
     *
     * @param code    错误码
     * @param message 错误提示
     */
    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 设置错误码
     *
     * @param code 错误码
     * @return 当前异常实例
     */
    public ServiceException setCode(Integer code) {
        this.code = code;
        return this;
    }

    /**
     * 获取错误提示
     *
     * @return 错误提示
     */
    @Override
    public String getMessage() {
        return message;
    }

    /**
     * 设置错误提示
     *
     * @param message 错误提示
     * @return 当前异常实例
     */
    public ServiceException setMessage(String message) {
        this.message = message;
        return this;
    }

}
