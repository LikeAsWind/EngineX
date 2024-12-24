package org.nstep.engine.framework.common.util.object;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.ArrayUtil;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.nstep.engine.framework.common.pojo.SortablePageParam;
import org.nstep.engine.framework.common.pojo.SortingField;
import org.springframework.util.Assert;

import static java.util.Collections.singletonList;

/**
 * {@link org.nstep.engine.framework.common.pojo.PageParam} 工具类
 * 提供与分页相关的工具方法，主要用于构建分页查询的起始位置、排序字段等。
 */
public class PageUtils {

    // 定义排序类型常量：升序和降序
    private static final Object[] ORDER_TYPES = new String[]{SortingField.ORDER_ASC, SortingField.ORDER_DESC};

    /**
     * 获取分页查询的起始位置
     * 根据当前页码和每页大小计算出查询的起始位置。
     *
     * @param pageParam 分页参数
     * @return 起始位置
     */
    public static int getStart(PageParam pageParam) {
        // 计算分页查询的起始位置
        return (pageParam.getPageNo() - 1) * pageParam.getPageSize();
    }

    /**
     * 构建排序字段（默认倒序）
     * 根据提供的 Lambda 表达式构建排序字段，默认排序类型为倒序（降序）。
     *
     * @param func 排序字段的 Lambda 表达式
     * @param <T>  排序字段所属的类型
     * @return 排序字段
     */
    public static <T> SortingField buildSortingField(Func1<T, ?> func) {
        // 默认排序类型为倒序
        return buildSortingField(func, SortingField.ORDER_DESC);
    }

    /**
     * 构建排序字段
     * 根据提供的 Lambda 表达式和排序类型构建排序字段。
     *
     * @param func  排序字段的 Lambda 表达式
     * @param order 排序类型 {@link SortingField#ORDER_ASC} 或 {@link SortingField#ORDER_DESC}
     * @param <T>   排序字段所属的类型
     * @return 排序字段
     */
    public static <T> SortingField buildSortingField(Func1<T, ?> func, String order) {
        // 校验排序类型是否合法
        Assert.isTrue(ArrayUtil.contains(ORDER_TYPES, order), String.format("字段的排序类型只能是 %s/%s", ORDER_TYPES));

        // 获取排序字段的名称
        String fieldName = LambdaUtil.getFieldName(func);
        // 构建并返回排序字段
        return new SortingField(fieldName, order);
    }

    /**
     * 构建默认的排序字段
     * 如果排序字段为空，则设置默认排序字段；否则忽略已有的排序字段。
     *
     * @param sortablePageParam 排序分页查询参数
     * @param func              排序字段的 Lambda 表达式
     * @param <T>               排序字段所属的类型
     */
    public static <T> void buildDefaultSortingField(SortablePageParam sortablePageParam, Func1<T, ?> func) {
        // 如果分页查询参数存在且排序字段为空，则设置默认排序字段
        if (sortablePageParam != null && CollUtil.isEmpty(sortablePageParam.getSortingFields())) {
            sortablePageParam.setSortingFields(singletonList(buildSortingField(func)));
        }
    }

}
