package org.nstep.engine.framework.excel.core.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.poi.excel.ExcelUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFDataValidation;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.nstep.engine.framework.common.core.KeyValue;
import org.nstep.engine.framework.dict.core.DictFrameworkUtils;
import org.nstep.engine.framework.excel.core.annotations.ExcelColumnSelect;
import org.nstep.engine.framework.excel.core.function.ExcelColumnSelectFunction;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.nstep.engine.framework.common.util.collection.CollectionUtils.convertList;

/**
 * 基于固定 sheet 实现下拉框
 * <p>
 * 该类用于在 Excel 中创建下拉框，具体做法是通过一个单独的 sheet（"字典sheet"）来存放下拉框的数据源。
 * 然后在目标 sheet 中的指定列插入下拉框，供用户选择。
 * 下拉框的数据源可以来自字典数据或自定义方法。
 */
@Slf4j
public class SelectSheetWriteHandler implements SheetWriteHandler {

    /**
     * 数据起始行从 0 开始
     * <p>
     * 约定：本项目第一行有标题，所以数据从第 2 行开始（索引为 1）。如果您的 Excel 有多行标题，请自行调整此值。
     */
    public static final int FIRST_ROW = 1;

    /**
     * 下拉列需要创建下拉框的行数，默认两千行。若需要更多行，请自行调整此值。
     */
    public static final int LAST_ROW = 2000;

    /**
     * 字典 sheet 的名称，用于存放所有的下拉数据源。
     */
    private static final String DICT_SHEET_NAME = "字典sheet";

    /**
     * 存储每一列的下拉数据源，key 为列索引，value 为对应的下拉数据源。
     */
    private final Map<Integer, List<String>> selectMap = new HashMap<>();

    /**
     * 构造方法，初始化下拉数据源。
     * <p>
     * 该方法会解析传入类的字段，查找被 @ExcelColumnSelect 注解标记的字段，并获取对应的下拉数据源。
     *
     * @param head 要解析的类（通常是 Excel 数据的实体类）
     */
    public SelectSheetWriteHandler(Class<?> head) {
        // 解析下拉数据
        int colIndex = 0;
        for (Field field : head.getDeclaredFields()) {
            // 查找带有 @ExcelColumnSelect 注解的字段
            if (field.isAnnotationPresent(ExcelColumnSelect.class)) {
                ExcelProperty excelProperty = field.getAnnotation(ExcelProperty.class);
                if (excelProperty != null && excelProperty.index() != -1) {
                    colIndex = excelProperty.index(); // 获取列索引
                }
                getSelectDataList(colIndex, field); // 获取该列的下拉数据源
            }
            colIndex++;
        }
    }

    /**
     * 设置单元格下拉选择
     * <p>
     * 该方法用于为 Excel 的指定列创建下拉框，并设置相关的验证约束。
     *
     * @param writeSheetHolder 写入的 sheet 对象
     * @param workbook         工作簿对象
     * @param helper           数据验证助手
     * @param keyValue         存储列索引和下拉数据源的键值对
     */
    private static void setColumnSelect(WriteSheetHolder writeSheetHolder, Workbook workbook, DataValidationHelper helper,
                                        KeyValue<Integer, List<String>> keyValue) {
        // 1.1 创建可被其他单元格引用的名称
        Name name = workbook.createName();
        String excelColumn = ExcelUtil.indexToColName(keyValue.getKey());
        // 1.2 下拉框数据来源 eg:字典sheet!$B1:$B2
        String refers = DICT_SHEET_NAME + "!$" + excelColumn + "$1:$" + excelColumn + "$" + keyValue.getValue().size();
        name.setNameName("dict" + keyValue.getKey()); // 设置名称的名字
        name.setRefersToFormula(refers); // 设置公式

        // 2.1 设置约束
        DataValidationConstraint constraint = helper.createFormulaListConstraint("dict" + keyValue.getKey()); // 设置引用约束
        // 设置下拉单元格的首行、末行、首列、末列
        CellRangeAddressList rangeAddressList = new CellRangeAddressList(FIRST_ROW, LAST_ROW,
                keyValue.getKey(), keyValue.getKey());
        DataValidation validation = helper.createValidation(constraint, rangeAddressList);
        if (validation instanceof HSSFDataValidation) {
            validation.setSuppressDropDownArrow(false); // 显示下拉箭头
        } else {
            validation.setSuppressDropDownArrow(true); // 隐藏下拉箭头
            validation.setShowErrorBox(true); // 显示错误框
        }
        // 2.2 阻止输入非下拉框的值
        validation.setErrorStyle(DataValidation.ErrorStyle.STOP);
        validation.createErrorBox("提示", "此值不存在于下拉选择中！");
        // 2.3 添加下拉框约束
        writeSheetHolder.getSheet().addValidationData(validation);
    }

    /**
     * 获得下拉数据，并添加到 {@link #selectMap} 中
     * <p>
     * 该方法根据列索引和字段获取下拉数据源，支持两种方式：
     * 1. 使用字典类型（dictType）获取下拉数据。
     * 2. 使用自定义方法名称（functionName）获取下拉数据。
     *
     * @param colIndex 列索引
     * @param field    字段
     */
    private void getSelectDataList(int colIndex, Field field) {
        ExcelColumnSelect columnSelect = field.getAnnotation(ExcelColumnSelect.class);
        String dictType = columnSelect.dictType();
        String functionName = columnSelect.functionName();
        Assert.isTrue(ObjectUtil.isNotEmpty(dictType) || ObjectUtil.isNotEmpty(functionName),
                "Field({}) 的 @ExcelColumnSelect 注解，dictType 和 functionName 不能同时为空", field.getName());

        // 情况一：使用 dictType 获得下拉数据
        if (StrUtil.isNotEmpty(dictType)) {
            selectMap.put(colIndex, DictFrameworkUtils.getDictDataLabelList(dictType)); // 使用字典数据
            return;
        }

        // 情况二：使用 functionName 获得下拉数据
        Map<String, ExcelColumnSelectFunction> functionMap = SpringUtil.getApplicationContext().getBeansOfType(ExcelColumnSelectFunction.class);
        ExcelColumnSelectFunction function = CollUtil.findOne(functionMap.values(), item -> item.getName().equals(functionName));
        Assert.notNull(function, "未找到对应的 function({})", functionName);
        selectMap.put(colIndex, function.getOptions()); // 使用自定义方法获取数据
    }

    /**
     * 在 Excel 工作簿创建后，设置下拉框数据。
     * <p>
     * 该方法会在 Excel sheet 创建后执行，主要用于设置字典 sheet 和目标 sheet 中的下拉框。
     *
     * @param writeWorkbookHolder 写入工作簿的操作对象
     * @param writeSheetHolder    写入 sheet 的操作对象
     */
    @Override
    public void afterSheetCreate(WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
        if (CollUtil.isEmpty(selectMap)) {
            return;
        }

        // 1. 获取相应操作对象
        DataValidationHelper helper = writeSheetHolder.getSheet().getDataValidationHelper(); // 需要设置下拉框的 sheet 页的数据验证助手
        Workbook workbook = writeWorkbookHolder.getWorkbook(); // 获得工作簿
        List<KeyValue<Integer, List<String>>> keyValues = convertList(selectMap.entrySet(), entry -> new KeyValue<>(entry.getKey(), entry.getValue()));
        keyValues.sort(Comparator.comparing(item -> item.getValue().size())); // 升序排序，以避免创建下拉框时出错

        // 2. 创建数据字典的 sheet 页
        Sheet dictSheet = workbook.createSheet(DICT_SHEET_NAME);
        for (KeyValue<Integer, List<String>> keyValue : keyValues) {
            int rowLength = keyValue.getValue().size();
            // 2.1 设置字典 sheet 页的值，每一列一个字典项
            for (int i = 0; i < rowLength; i++) {
                Row row = dictSheet.getRow(i);
                if (row == null) {
                    row = dictSheet.createRow(i);
                }
                row.createCell(keyValue.getKey()).setCellValue(keyValue.getValue().get(i));
            }
            // 2.2 设置单元格下拉选择
            setColumnSelect(writeSheetHolder, workbook, helper, keyValue);
        }
    }

}
