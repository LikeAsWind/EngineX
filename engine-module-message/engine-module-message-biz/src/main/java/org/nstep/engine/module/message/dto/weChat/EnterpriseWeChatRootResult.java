package org.nstep.engine.module.message.dto.weChat;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业微信机器人返回数据类型
 * 该类用于封装企业微信机器人发送请求后的响应数据，包含错误码、错误信息、文件类型等信息。
 */
@NoArgsConstructor 
@Data 
@AllArgsConstructor 
@Builder 
public class EnterpriseWeChatRootResult {

    /**
     * 错误码
     * 0 为成功，其他值表示失败，具体错误信息可通过 errMsg 获取。
     */
    @JSONField(name = "errcode")
    private Integer errCode;

    /**
     * 错误信息
     * 如果请求失败，返回错误的具体描述。
     */
    @JSONField(name = "errmsg")
    private String errMsg;

    /**
     * 文件类型
     * 该字段指示上传的文件类型，可能的值包括：语音(voice)和普通文件(file)。
     */
    @JSONField(name = "type")
    private String type;

    /**
     * 媒体文件唯一标识
     * 上传媒体文件后，返回的唯一标识符。该标识符在3天内有效。
     */
    @JSONField(name = "media_id")
    private String mediaId;

    /**
     * 上传时间戳
     * 媒体文件上传的时间戳，表示文件上传时的具体时间。
     */
    @JSONField(name = "created_at")
    private String createdAt;
}
