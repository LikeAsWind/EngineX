package org.nstep.engine.module.message.dto.push.getui;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 个推响应对象
 * 该类用于封装个推推送请求的响应结果，包含响应消息、响应码以及附加的数据。
 */
@Builder 
@Data 
@AllArgsConstructor 
@NoArgsConstructor 
public class SendPushResult {

    /**
     * 响应消息
     * 该字段包含个推服务器返回的消息，通常用于描述请求处理的状态或错误信息。
     */
    @JSONField(name = "msg")
    private String msg;

    /**
     * 响应码
     * 该字段包含个推服务器返回的响应码，用于标识请求的处理结果。
     * 例如，0表示成功，其他数字可能表示不同的错误类型。
     */
    @JSONField(name = "code")
    private Integer code;

    /**
     * 附加数据
     * 该字段包含个推服务器返回的附加数据，通常为JSON格式，包含请求的详细信息或处理结果。
     */
    @JSONField(name = "data")
    private JSONObject data;

}
