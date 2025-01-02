package org.nstep.engine.module.message.dto.push.getui;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取用户个推token请求响应对象
 * 该类用于封装从个推服务器获取token的响应结果，包括响应消息、状态码和token数据。
 */
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class GeTuiTokenResultDTO {

    /**
     * 响应消息
     * 用于描述请求的结果信息，通常为成功或失败的提示。
     */
    @JSONField(name = "msg")
    private String msg;

    /**
     * 响应状态码
     * 用于标识请求的执行状态，通常用于判断请求是否成功，0表示成功，非0表示失败。
     */
    @JSONField(name = "code")
    private Integer code;

    /**
     * token数据
     * 封装了token信息，包含token的值和过期时间。
     */
    @JSONField(name = "data")
    private DataDTO data;

    /**
     * Token数据对象
     * 用于封装获取到的token信息，包括token的值和过期时间。
     */
    @NoArgsConstructor 
    @Data 
    public static class DataDTO {

        /**
         * token的过期时间
         * 用于标识token的有效期，过期后需要重新获取。
         */
        @JSONField(name = "expire_time")
        private String expireTime;

        /**
         * 获取到的token值
         * 用于进行后续的API请求认证，必须在有效期内使用。
         */
        @JSONField(name = "token")
        private String token;
    }
}
