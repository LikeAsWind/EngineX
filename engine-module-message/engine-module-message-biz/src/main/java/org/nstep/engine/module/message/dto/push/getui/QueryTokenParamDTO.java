package org.nstep.engine.module.message.dto.push.getui;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询用户个推token参数
 * 该类用于封装查询个推token时所需的请求参数，包括签名、时间戳和应用的appkey。
 */
@NoArgsConstructor 
@Data 
@Builder // 支持构建者模式
@AllArgsConstructor 
public class QueryTokenParamDTO {

    /**
     * 签名
     * 用于验证请求的合法性，通常是根据特定算法生成的加密字符串。
     */
    @JSONField(name = "sign")
    private String sign;

    /**
     * 时间戳
     * 用于标识请求的发送时间，通常用于防止重放攻击，确保请求的时效性。
     */
    @JSONField(name = "timestamp")
    private String timestamp;

    /**
     * 应用的appkey
     * 用于标识请求来源的应用，每个应用都有唯一的appkey。
     */
    @JSONField(name = "appkey")
    private String appKey;
}
