package org.nstep.engine.framework.excel.core.function;

import java.util.List;

/**
 * Excel 列下拉数据源获取接口
 * <p>
 * 该接口用于获取 Excel 列的下拉选择数据源。虽然通常可以通过字典数据来提供下拉选项，但考虑到某些情况下下拉数据并非来自字典，
 * 因此需要通过接口来提供统一的获取方式，以兼容更多的数据源。
 */
public interface ExcelColumnSelectFunction {

    /**
     * 获得方法名称
     * <p>
     * 获取该数据源的名称，通常用于标识该数据源，或者用于反射调用相关的方法。
     *
     * @return 方法名称，表示该数据源的唯一标识
     */
    String getName();

    /**
     * 获得列下拉数据源
     * <p>
     * 获取该列的所有下拉选项。返回一个字符串列表，列表中的每个元素代表一个下拉选项。
     *
     * @return 下拉数据源，返回一个包含所有选项的字符串列表
     */
    List<String> getOptions();

}
