package org.nstep.engine.module.message.dto.push.getui;


import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 个推cid批量推送请求参数
 * 该类用于封装个推批量推送请求的参数，包括目标受众、任务ID和是否异步执行的标识。
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class BatchSendPushParam {

    /**
     * 受众信息
     * 用于指定推送消息的目标受众，可以是多个cid（客户端ID）。
     */
    @JSONField(name = "audience") // JSON序列化时，字段名为"audience"
    private AudienceVO audience;

    /**
     * 任务ID
     * 用于标识该批量推送任务的唯一标识符。
     */
    @JSONField(name = "taskid") // JSON序列化时，字段名为"taskid"
    private String taskId;

    /**
     * 是否异步
     * 用于标识该推送任务是否异步执行，true表示异步，false表示同步。
     */
    @JSONField(name = "is_async") // JSON序列化时，字段名为"is_async"
    private Boolean isAsync;

    /**
     * AudienceVO
     * 该类用于封装受众信息，包含cid集合。
     */
    @NoArgsConstructor
    @Data
    @Builder
    @AllArgsConstructor
    public static class AudienceVO {
        /**
         * cid集合
         * 该字段用于指定接收推送消息的设备ID集合。
         */
        @JSONField(name = "cid")
        private Set<String> cid;
    }
}
