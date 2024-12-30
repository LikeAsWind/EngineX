package org.nstep.engine.module.message.dto.content;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 飞书机器人消息体
 * <p>
 * 该类用于表示飞书机器人发送的消息体。飞书机器人支持不同类型的消息，本类主要用于构建飞书机器人的消息内容。
 * 消息体包含消息类型（msgType）以及具体的文本内容（text）。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeiShuRobotContentModel extends ContentModel {

    /**
     * 消息类型
     * <p>
     * 该字段用于指定消息的类型，例如文本消息、图片消息等。不同的消息类型会影响消息内容的格式。
     */
    private String msgType;

    /**
     * 文本内容
     * <p>
     * 该字段用于存储文本类型的消息内容。它包含具体的文本信息，供飞书机器人发送。
     */
    private Text text;

    /**
     * 文本内容类
     * <p>
     * 用于存储飞书机器人文本消息的具体内容。
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class Text {
        /**
         * 消息内容
         * <p>
         * 该字段用于存储飞书机器人文本消息的具体内容，通常是一个字符串。
         */
        private String content;
    }
}
