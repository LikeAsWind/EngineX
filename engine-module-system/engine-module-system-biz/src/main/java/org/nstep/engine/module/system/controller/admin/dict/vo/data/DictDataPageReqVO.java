package org.nstep.engine.module.system.controller.admin.dict.vo.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nstep.engine.framework.common.enums.CommonStatusEnum;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.nstep.engine.framework.common.validation.InEnum;

/**
 * 管理后台 - 字典类型分页列表请求 VO
 * <p>
 * 该类用于接收前端请求的字典类型分页列表的查询条件，继承自 `PageParam`，包括字典标签、字典类型和展示状态等参数。
 */
@Schema(description = "管理后台 - 字典类型分页列表 Request VO")
@Data
@EqualsAndHashCode(callSuper = true) // 继承 PageParam，自动生成 equals 和 hashCode 方法
public class DictDataPageReqVO extends PageParam {

    /**
     * 字典标签
     * <p>
     * 用于模糊匹配字典的标签名称，最大长度为 100 个字符。
     */
    @Schema(description = "字典标签", example = "engine")
    @Size(max = 100, message = "字典标签长度不能超过100个字符")
    private String label;

    /**
     * 字典类型
     * <p>
     * 用于模糊匹配字典的类型，最大长度为 100 个字符。
     */
    @Schema(description = "字典类型，模糊匹配", example = "sys_common_sex")
    @Size(max = 100, message = "字典类型类型长度不能超过100个字符")
    private String dictType;

    /**
     * 展示状态
     * <p>
     * 用于过滤字典数据的展示状态，值必须是 `CommonStatusEnum` 枚举类中的某个值。
     */
    @Schema(description = "展示状态，参见 CommonStatusEnum 枚举类", example = "1")
    @InEnum(value = CommonStatusEnum.class, message = "修改状态必须是 {value}")
    private Integer status;

}
