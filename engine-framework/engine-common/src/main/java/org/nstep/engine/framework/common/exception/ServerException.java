package org.nstep.engine.framework.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.nstep.engine.framework.common.exception.enums.GlobalErrorCodeConstants;

/**
 * 服务器异常 Exception
 */
@Data
@EqualsAndHashCode(callSuper = true)
public final class ServerException extends RuntimeException {

    /**
     * 全局错误码
     * -- GETTER --
     * 获取错误码
     *
     * @see GlobalErrorCodeConstants
     */
    @Getter
    private Integer code;
    /**
     * 错误提示
     */
    private String message;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServerException() {
    }

    /**
     * 构造方法，根据错误码枚举创建异常
     *
     * @param errorCode 错误码枚举
     */
    public ServerException(ErrorCode errorCode) {
        this.code = errorCode.getCode();
        this.message = errorCode.getMsg();
    }

    /**
     * 构造方法，根据错误码和错误提示创建异常
     *
     * @param code    错误码
     * @param message 错误提示
     */
    public ServerException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 设置错误码
     *
     * @param code 错误码
     * @return 当前异常实例
     */
    public ServerException setCode(Integer code) {
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
    public ServerException setMessage(String message) {
        this.message = message;
        return this;
    }

}

