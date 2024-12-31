package org.nstep.engine.module.message.dto.dingding;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 发送钉钉自定义机器人请求参数类
 * <a href =https://open.dingtalk.com/document/group/custom-robot-access></a>
 * 该类用于构建发送给钉钉自定义机器人消息的请求参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingRobotParam {

    /**
     * at参数，指定@的对象（人员或全员）
     */
    private At at;

    /**
     * text参数，发送的文本消息内容
     */
    private Text text;

    /**
     * link参数，发送的链接消息内容
     */
    private Link link;

    /**
     * markdown参数，发送的markdown格式消息内容
     */
    private Markdown markdown;

    /**
     * actionCard参数，发送的ActionCard类型消息内容
     */
    private ActionCard actionCard;

    /**
     * feedCard参数，发送的FeedCard类型消息内容
     */
    private FeedCard feedCard;

    /**
     * msgtype参数，消息类型（如text, link, markdown等）
     */
    private String msgtype;

    /**
     * At类，用于指定@的人员信息
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class At {

        /**
         * atMobiles，指定@的手机号列表
         */
        private List<String> atMobiles;

        /**
         * atUserIds，指定@的用户ID列表
         */
        private List<String> atUserIds;

        /**
         * isAtAll，是否@所有人（true为@所有人，false为不@所有人）
         */
        private Boolean isAtAll;
    }

    /**
     * Text类，表示文本消息的内容
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class Text {

        /**
         * content，文本消息的具体内容
         */
        private String content;
    }

    /**
     * Link类，表示链接类型消息
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class Link {

        /**
         * text，链接消息的文本内容
         */
        private String text;

        /**
         * title，链接消息的标题
         */
        private String title;

        /**
         * picUrl，链接消息的图片URL
         */
        private String picUrl;

        /**
         * messageUrl，链接消息的跳转URL
         */
        private String messageUrl;
    }

    /**
     * Markdown类，表示markdown类型消息
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class Markdown {

        /**
         * title，markdown消息的标题
         */
        private String title;

        /**
         * text，markdown消息的具体内容
         */
        private String text;
    }

    /**
     * ActionCard类，表示ActionCard类型消息
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class ActionCard {

        /**
         * title，ActionCard的标题
         */
        private String title;

        /**
         * text，ActionCard的文本内容
         */
        private String text;

        /**
         * btnOrientation，按钮排列方式（horizontal：横向排列，vertical：纵向排列）
         */
        private String btnOrientation;

        /**
         * btns，ActionCard中的按钮列表
         */
        private List<Btns> btns;

        /**
         * Btns类，表示ActionCard中的按钮
         */
        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class Btns {

            /**
             * title，按钮的标题
             */
            private String title;

            /**
             * actionURL，按钮点击后的跳转URL
             */
            @JSONField(name = "actionURL")
            private String actionUrl;
        }
    }

    /**
     * FeedCard类，表示FeedCard类型消息
     */
    @NoArgsConstructor
    @Data
    @AllArgsConstructor
    @Builder
    public static class FeedCard {

        /**
         * links，FeedCard中的链接列表
         */
        private List<Links> links;

        /**
         * Links类，表示FeedCard中的每个链接
         */
        @NoArgsConstructor
        @Data
        @AllArgsConstructor
        @Builder
        public static class Links {

            /**
             * title，FeedCard链接的标题
             */
            private String title;

            /**
             * messageURL，FeedCard链接的跳转URL
             */
            @JSONField(name = "messageURL")
            private String messageUrl;

            /**
             * picURL，FeedCard链接的图片URL
             */
            @JSONField(name = "picURL")
            private String picUrl;
        }
    }
}
