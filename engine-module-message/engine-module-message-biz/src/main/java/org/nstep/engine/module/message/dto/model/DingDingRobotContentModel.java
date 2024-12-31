package org.nstep.engine.module.message.dto.model;

import lombok.*;

/**
 * 钉钉自定义机器人消息内容模型
 * <p>
 * 该类用于表示钉钉自定义机器人消息的内容模型。根据钉钉自定义机器人API，消息内容可以有不同的类型，如文本消息、markdown消息、ActionCard消息等。
 * 该类继承自ContentModel，适用于构建钉钉自定义机器人发送的消息内容。
 * <a href="https://open.dingtalk.com/document/group/custom-robot-access">钉钉自定义机器人接入文档</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DingDingRobotContentModel extends ContentModel {

    /**
     * 发送类型
     * <p>
     * 该字段用于指定消息的发送类型，例如文本消息、markdown消息等。具体类型会影响消息的格式和内容。
     */
    private String sendType;

    /**
     * 钉钉机器人消息内容
     * <p>
     * 根据不同的发送类型（如文本消息、markdown消息、ActionCard消息等），该字段存储相应的消息内容。
     * 例如，文本消息存储纯文本，markdown消息存储markdown格式的文本，ActionCard消息存储ActionCard格式内容等。
     */
    private String content;

}
