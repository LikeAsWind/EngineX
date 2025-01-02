package org.nstep.engine.module.message.dto.push.getui;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 个推cid单推发送请求参数
 * 该类用于封装个推单推请求的参数，包括请求ID、推送设置、接收者信息以及推送消息内容。
 */
@NoArgsConstructor 
@AllArgsConstructor 
@Data 
@Builder 
public class SingleSendPushParam {

    /**
     * 请求ID
     * 该字段用于标识单次推送请求的唯一性，每次请求应该生成一个新的唯一ID。
     */
    @JSONField(name = "request_id")
    private String requestId;

    /**
     * 推送设置
     * 该字段包含推送请求的设置，例如推送的有效时间等。
     */
    @JSONField(name = "settings")
    private SettingsVO settings;

    /**
     * 接收者信息
     * 该字段包含接收推送消息的用户的cid列表。
     */
    @JSONField(name = "audience")
    private AudienceVO audience;

    /**
     * 推送消息
     * 该字段包含推送的消息内容，包括通知的标题、正文等信息。
     */
    @JSONField(name = "push_message")
    private PushMessageVO pushMessage;

    /**
     * 推送设置
     * 该类用于封装推送的设置，例如推送的生存时间（TTL）。
     */
    @NoArgsConstructor 
    @Data // 自动生成getter、setter等方法
    public static class SettingsVO {
        /**
         * TTL（生存时间）
         * 该字段表示推送消息的有效时间，单位为秒。超过该时间，消息将失效。
         */
        @JSONField(name = "ttl")
        private Integer ttl;
    }

    /**
     * 接收者信息
     * 该类包含接收推送消息的用户的cid（客户端ID）列表。
     */
    @NoArgsConstructor 
    @Data // 自动生成getter、setter等方法
    @AllArgsConstructor 
    @Builder 
    public static class AudienceVO {
        /**
         * cid列表
         * 该字段包含接收推送消息的用户的cid集合。
         */
        @JSONField(name = "cid")
        private Set<String> cid;
    }

    /**
     * 推送消息
     * 该类包含推送的消息内容，例如通知的标题、正文、点击类型等信息。
     */
    @NoArgsConstructor 
    @Data // 自动生成getter、setter等方法
    @AllArgsConstructor 
    @Builder 
    public static class PushMessageVO {
        /**
         * 通知内容
         * 该字段包含推送通知的详细内容，例如标题、正文、点击类型等。
         */
        @JSONField(name = "notification")
        private NotificationVO notification;

        /**
         * 通知内容
         * 该类包含推送通知的详细信息，包括标题、正文、点击类型、跳转链接等。
         */
        @NoArgsConstructor 
        @Data // 自动生成getter、setter等方法
        @AllArgsConstructor 
        @Builder 
        public static class NotificationVO {
            /**
             * 标题
             * 该字段表示推送通知的标题。
             */
            @JSONField(name = "title")
            private String title;

            /**
             * 正文
             * 该字段表示推送通知的正文内容。
             */
            @JSONField(name = "body")
            private String body;

            /**
             * 渠道级别
             * 该字段表示推送的渠道级别，用于控制消息的优先级或显示方式。
             */
            @JSONField(name = "channel_level")
            private String channelLevel;

            /**
             * 点击类型
             * 该字段表示点击通知后的行为类型，例如打开链接、打开应用等。
             */
            @JSONField(name = "click_type")
            private String clickType;

            /**
             * 跳转URL
             * 该字段表示点击通知后跳转的URL地址，仅当点击类型为URL时有效。
             */
            @JSONField(name = "url")
            private String url;

            /**
             * 跳转Intent
             * 该字段表示点击通知后跳转的Intent，仅当点击类型为Intent时有效。
             */
            @JSONField(name = "intent")
            private String intent;

            /**
             * Payload数据
             * 该字段用于携带额外的数据，仅当点击类型为payload或payload_custom时有效。
             */
            @JSONField(name = "payload")
            private String payload;
        }
    }
}
