package org.nstep.engine.framework.excel.core.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.nstep.engine.framework.common.util.json.JsonUtils;

/**
 * Excel Json 转换器
 * <p>
 * 用于将 Java 对象转换为 JSON 字符串并写入 Excel 文件。
 * 该转换器将 Java 对象转换为 JSON 格式的字符串，以便在 Excel 中存储。
 * 目前不支持将 Excel 数据转换回 Java 对象，因此只实现了 `convertToExcelData` 方法。
 */
public class JsonConvert implements Converter<Object> {

    /**
     * 获取支持的 Java 类型键。
     * <p>
     * 由于本转换器不支持将 Excel 数据转换回 Java 对象，因此该方法不支持，调用时会抛出 {@link UnsupportedOperationException} 异常。
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
     * 由于本转换器不支持将 Excel 数据转换回 Java 对象，因此该方法不支持，调用时会抛出 {@link UnsupportedOperationException} 异常。
     *
     * @return 不返回任何值
     * @throws UnsupportedOperationException 始终抛出异常
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        throw new UnsupportedOperationException("暂不支持，也不需要");
    }

    /**
     * 将 Java 对象转换为 Excel 单元格数据。
     * <p>
     * 将 Java 对象转换为 JSON 字符串，并作为 Excel 单元格数据写入。
     * 使用 {@link JsonUtils#toJsonString(Object)} 方法将对象转换为 JSON 格式的字符串。
     *
     * @param value               Java 对象，需要转换为 JSON 字符串
     * @param contentProperty     当前字段的属性信息
     * @param globalConfiguration 全局配置
     * @return 转换后的 Excel 单元格数据（JSON 字符串）
     */
    @Override
    public WriteCellData<String> convertToExcelData(Object value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        // 将 Java 对象转换为 JSON 字符串
        return new WriteCellData<>(JsonUtils.toJsonString(value));
    }
}
