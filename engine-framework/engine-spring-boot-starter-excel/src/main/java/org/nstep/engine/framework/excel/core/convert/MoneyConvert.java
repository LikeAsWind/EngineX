package org.nstep.engine.framework.excel.core.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额转换器
 * <p>
 * 用于将金额从分转换为元，并以字符串形式写入 Excel。
 * 金额单位默认为分，转换时将分数值除以 100，得到元单位的金额，保留两位小数。
 */
public class MoneyConvert implements Converter<Integer> {

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
     * 将金额从分转换为元，并转换为 Excel 单元格数据。
     * <p>
     * 将分单位的金额转换为元，保留两位小数。转换公式为：金额（元） = 金额（分） / 100。
     * 转换后将金额以字符串形式返回，供 Excel 写入。
     *
     * @param value               金额，单位为分
     * @param contentProperty     当前字段的属性信息
     * @param globalConfiguration 全局配置
     * @return 转换后的 Excel 单元格数据（金额，单位为元）
     */
    @Override
    public WriteCellData<String> convertToExcelData(Integer value, ExcelContentProperty contentProperty,
                                                    GlobalConfiguration globalConfiguration) {
        // 将分转换为元，并保留两位小数
        BigDecimal result = BigDecimal.valueOf(value)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        // 返回转换后的金额，作为字符串
        return new WriteCellData<>(result.toString());
    }
}
