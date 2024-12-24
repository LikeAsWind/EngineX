package org.nstep.engine.framework.excel.core.convert;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.ip.core.Area;
import org.nstep.engine.framework.ip.core.utils.AreaUtils;

/**
 * Excel 数据地区转换器
 * <p>
 * 用于将 Excel 单元格中的地区名称（如“北京市”）解析为对应的地区编号。
 * 适用于读取 Excel 文件时，自动将地区名称转换为系统内部使用的地区编号。
 */
@Slf4j // 日志记录支持
public class AreaConvert implements Converter<Object> {

    /**
     * 获取支持的 Java 类型键。
     * <p>
     * 由于本转换器无需明确指定支持的 Java 类型键，因此不支持该方法。
     * 调用该方法将抛出 {@link UnsupportedOperationException} 异常。
     *
     * @return 不返回任何值
     * @throws UnsupportedOperationException 始终抛出异常
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    /**
     * 获取支持的 Excel 类型键。
     * <p>
     * 由于本转换器无需明确指定支持的 Excel 类型键，因此不支持该方法。
     * 调用该方法将抛出 {@link UnsupportedOperationException} 异常。
     *
     * @return 不返回任何值
     * @throws UnsupportedOperationException 始终抛出异常
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    /**
     * 将 Excel 单元格数据转换为 Java 数据。
     * <p>
     * 解析 Excel 单元格中的地区名称（字符串），并转换为地区编号。
     * 如果无法解析地区名称，将记录错误日志并返回 null。
     *
     * @param readCellData        Excel 单元格数据，包含需要解析的地区名称
     * @param contentProperty     当前字段的属性信息
     * @param globalConfiguration 全局配置
     * @return 转换后的 Java 数据（地区编号），或者 null（解析失败）
     */
    @Override
    public Object convertToJavaData(ReadCellData readCellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        // 从单元格数据中获取地区名称
        String label = readCellData.getStringValue();
        // 解析地区名称为地区对象
        Area area = AreaUtils.parseArea(label);
        if (area == null) {
            // 记录解析失败的日志
            log.error("[convertToJavaData][label({}) 解析不掉]", label);
            return null;
        }
        // 获取目标字段的类型
        Class<?> fieldClazz = contentProperty.getField().getType();
        // 将地区编号转换为目标字段类型的值
        return Convert.convert(fieldClazz, area.getId());
    }
}
