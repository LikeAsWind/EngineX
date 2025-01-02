package org.nstep.engine.module.message.dto.tencent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询腾讯云短信回执参数
 * 该类用于封装查询腾讯云短信回执的请求参数，包含手机号、时间范围、分页信息等。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryTencentCloudSMSReceiptParam {

    /**
     * 手机号
     * 该字段用于指定查询短信回执的目标手机号。
     * 必选参数。
     */
    private String phone;

    /**
     * 开始时间
     * 该字段表示查询的开始时间，时间戳精确到秒。
     * 必选参数。
     */
    private Long beginTime;

    /**
     * 查询页开始位置
     * 该字段表示查询的起始位置，用于分页查询。
     * 必选参数。
     */
    private Long offset;

    /**
     * 条数
     * 该字段表示每页查询的短信回执数量。
     * 必选参数。
     */
    private Long limit;
}
