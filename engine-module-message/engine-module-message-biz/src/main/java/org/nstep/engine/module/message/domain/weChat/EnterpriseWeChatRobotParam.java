package org.nstep.engine.module.message.domain.weChat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 企业微信机器人请求参数
 * 该类用于表示发送到企业微信机器人的消息内容
 * 参考文档: <a  href=https://developer.work.weixin.qq.com/document/path/91770></a>
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseWeChatRobotParam {

    /**
     * 消息类型msgtype
     * 用于指定消息的类型，例如文本、图片等
     */
    @JSONField(name = "msgtype")
    private String msgType;

    /**
     * text
     * 消息类型为文本时的内容
     */
    @JSONField(name = "text")
    private TextDTO text;

    /**
     * markdown
     * 消息类型为markdown时的内容
     */
    @JSONField(name = "markdown")
    private MarkdownDTO markdown;

    /**
     * image
     * 消息类型为图片时的内容
     */
    @JSONField(name = "image")
    private ImageDTO image;

    /**
     * news
     * 消息类型为图文时的内容
     */
    @JSONField(name = "news")
    private NewsDTO news;

    /**
     * file
     * 消息类型为文件时的内容
     */
    @JSONField(name = "file")
    private FileDTO file;

    /**
     * voice
     * 消息类型为语音时的内容
     */
    @JSONField(name = "voice")
    private VoiceDTO voice;


    /**
     * TextDTO
     * 用于封装文本消息的内容
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class TextDTO {
        /**
         * content
         * 文本消息的内容
         */
        @JSONField(name = "content")
        private String content;

        /**
         * mentionedList
         * 被@的用户列表（可以是用户的userid）
         */
        @JSONField(name = "mentioned_list")
        private List<String> mentionedList;

        /**
         * mentionedMobileList
         * 被@的手机号列表
         */
        @JSONField(name = "mentioned_mobile_list")
        private List<String> mentionedMobileList;
    }

    /**
     * MarkdownDTO
     * 用于封装markdown消息的内容
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class MarkdownDTO {
        /**
         * content
         * markdown消息的内容
         */
        @JSONField(name = "content")
        private String content;
    }

    /**
     * ImageDTO
     * 用于封装图片消息的内容
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class ImageDTO {
        /**
         * base64
         * 图片内容的base64编码
         */
        @JSONField(name = "base64")
        private String base64;

        /**
         * md5
         * 图片内容（base64编码前）的md5值，用于验证图片内容
         */
        @JSONField(name = "md5")
        private String md5;
    }

    /**
     * NewsDTO
     * 用于封装图文消息的内容
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class NewsDTO {
        /**
         * articles
         * 图文消息的文章列表
         */
        @JSONField(name = "articles")
        private List<ArticlesDTO> articles;

        /**
         * ArticlesDTO
         * 每篇文章的内容
         */
        @NoArgsConstructor
        @Data
        @Builder
        @AllArgsConstructor
        public static class ArticlesDTO {
            /**
             * title
             * 文章标题
             */
            @JSONField(name = "title")
            private String title;

            /**
             * description
             * 文章描述
             */
            @JSONField(name = "description")
            private String description;

            /**
             * url
             * 文章的链接
             */
            @JSONField(name = "url")
            private String url;

            /**
             * picurl
             * 文章的图片链接
             */
            @JSONField(name = "picurl")
            private String picurl;
        }
    }

    /**
     * FileDTO
     * 用于封装文件消息的内容
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class FileDTO {
        /**
         * mediaId
         * 文件的mediaId，企业微信用来标识文件
         */
        @JSONField(name = "media_id")
        private String media_id;
    }

    /**
     * VoiceDTO
     * 用于封装语音消息的内容
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class VoiceDTO {
        /**
         * mediaId
         * 语音的mediaId，企业微信用来标识语音
         */
        @JSONField(name = "media_id")
        private String media_id;
    }
}
