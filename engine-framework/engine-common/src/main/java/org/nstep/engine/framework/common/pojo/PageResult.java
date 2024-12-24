package org.nstep.engine.framework.common.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Schema(description = "分页结果")
@Data
public final class PageResult<T> implements Serializable {

    @Schema(description = "数据", requiredMode = Schema.RequiredMode.REQUIRED)
    private List<T> list;

    @Schema(description = "总量", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long total;

    /**
     * 构造方法
     */
    public PageResult() {
    }

    /**
     * 构造方法
     *
     * @param list  数据列表
     * @param total 数据总量
     */
    public PageResult(List<T> list, Long total) {
        this.list = list;
        this.total = total;
    }

    /**
     * 构造方法
     *
     * @param total 数据总量
     */
    public PageResult(Long total) {
        this.list = new ArrayList<>();
        this.total = total;
    }

    /**
     * 创建一个空的分页结果对象
     *
     * @param <T> 数据类型
     * @return 空的分页结果对象
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(0L);
    }

    /**
     * 创建一个指定总量的空分页结果对象
     *
     * @param total 数据总量
     * @param <T>   数据类型
     * @return 指定总量的空分页结果对象
     */
    public static <T> PageResult<T> empty(Long total) {
        return new PageResult<>(total);
    }

}
