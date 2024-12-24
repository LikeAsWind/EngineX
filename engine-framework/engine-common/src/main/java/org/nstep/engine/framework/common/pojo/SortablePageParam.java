package org.nstep.engine.framework.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

/**
 * 可排序的分页参数
 * 该类继承自 PageParam 类，用于封装可排序的分页查询参数
 */
@Schema(description = "可排序的分页参数")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class SortablePageParam extends PageParam {

    /**
     * 排序字段列表
     * 该列表包含了用于排序的字段信息
     */
    @Schema(description = "排序字段")
    private List<SortingField> sortingFields;

}
