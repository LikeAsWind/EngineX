package org.nstep.engine.framework.excel.core.convert;

import cn.hutool.core.convert.Convert;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.dict.core.DictFrameworkUtils;
import org.nstep.engine.framework.excel.core.annotations.DictFormat;

/**
 * Excel 数据字典转换器
 * <p>
 * 用于在 Excel 数据和系统内部数据之间进行字典转换。
 * 实现从字典值到字典标签的双向转换：
 * - 读取 Excel 文件时，将字典标签解析为字典值；
 * - 写入 Excel 文件时，将字典值格式化为字典标签。
 */
@Slf4j // 日志记录支持
public class DictConvert implements Converter<Object> {

    /**
     * 获取字段的字典类型。
     * <p>
     * 从字段的 {@link DictFormat} 注解中提取字典类型。
     *
     * @param contentProperty 字段的内容属性
     * @return 字典类型
     */
    private static String getType(ExcelContentProperty contentProperty) {
        return contentProperty.getField().getAnnotation(DictFormat.class).value();
    }

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
     * 根据字典类型，将 Excel 单元格中的字典标签解析为对应的字典值。
     * 如果无法解析字典标签，将记录错误日志并返回 null。
     *
     * @param readCellData        Excel 单元格数据，包含需要解析的字典标签
     * @param contentProperty     当前字段的属性信息
     * @param globalConfiguration 全局配置
     * @return 转换后的 Java 数据（字典值），或者 null（解析失败）
     */
    @Override
    public Object convertToJavaData(ReadCellData readCellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        // 从字段属性中获取字典类型
        String type = getType(contentProperty);
        // 获取单元格中的字典标签
        String label = readCellData.getStringValue();
        // 通过字典工具解析标签为值
        String value = DictFrameworkUtils.parseDictDataValue(type, label);
        if (value == null) {
            // 记录解析失败的日志
            log.error("[convertToJavaData][type({}) 解析不掉 label({})]", type, label);
            return null;
        }
        // 将解析得到的字典值转换为目标字段类型
        Class<?> fieldClazz = contentProperty.getField().getType();
        return Convert.convert(fieldClazz, value);
    }

    /**
     * 将 Java 数据转换为 Excel 单元格数据。
     * <p>
     * 根据字典类型，将 Java 数据中的字典值格式化为对应的字典标签。
     * 如果无法格式化字典值，将记录错误日志并返回空字符串。
     *
     * @param object              Java 数据，包含需要格式化的字典值
     * @param contentProperty     当前字段的属性信息
     * @param globalConfiguration 全局配置
     * @return 转换后的 Excel 单元格数据（字典标签）
     */
    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        // 如果值为空，返回空字符串
        if (object == null) {
            return new WriteCellData<>("");
        }

        // 从字段属性中获取字典类型
        String type = getType(contentProperty);
        // 获取 Java 数据中的字典值
        String value = String.valueOf(object);
        // 通过字典工具将值格式化为标签
        String label = DictFrameworkUtils.getDictDataLabel(type, value);
        if (label == null) {
            // 记录格式化失败的日志
            log.error("[convertToExcelData][type({}) 转换不了 label({})]", type, value);
            return new WriteCellData<>("");
        }
        // 返回格式化后的字典标签
        return new WriteCellData<>(label);
    }
}
