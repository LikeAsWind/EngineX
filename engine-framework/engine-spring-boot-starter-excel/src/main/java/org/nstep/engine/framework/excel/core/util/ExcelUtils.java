package org.nstep.engine.framework.excel.core.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.converters.longconverter.LongStringConverter;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import jakarta.servlet.http.HttpServletResponse;
import org.nstep.engine.framework.excel.core.handler.SelectSheetWriteHandler;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Excel 工具类
 * <p>
 * 该类提供了处理 Excel 文件的常用功能，包括将数据写入 Excel 文件并响应给前端，以及从上传的 Excel 文件读取数据。
 */
public class ExcelUtils {

    /**
     * 将列表数据写入 Excel 文件并以响应的形式返回给前端
     * <p>
     * 该方法会将数据列表以 Excel 文件的形式返回给客户端，客户端会收到一个下载 Excel 文件的响应。
     * 通过 `EasyExcel` 库进行 Excel 的写入操作，支持自定义 Excel sheet 名称、文件名、头部信息等。
     *
     * @param response  响应对象，用于向前端返回文件
     * @param filename  要下载的 Excel 文件名
     * @param sheetName Excel sheet 的名称
     * @param head      Excel 文件的表头（即类的字段定义）
     * @param data      要写入的数据列表
     * @param <T>       泛型，保证传入的 head 和 data 类型一致
     * @throws IOException 写入 Excel 文件时可能发生的 IO 异常
     */
    public static <T> void write(HttpServletResponse response, String filename, String sheetName,
                                 Class<T> head, List<T> data) throws IOException {
        // 使用 EasyExcel 写入数据到 Excel
        EasyExcel.write(response.getOutputStream(), head)
                .autoCloseStream(false) // 不要自动关闭流，由 Servlet 自己处理
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy()) // 根据列长度自动适配宽度，最大 255
                .registerWriteHandler(new SelectSheetWriteHandler(head)) // 为 Excel 中的列添加下拉框
                .registerConverter(new LongStringConverter()) // 避免 Long 类型在 Excel 中丢失精度
                .sheet(sheetName).doWrite(data); // 将数据写入指定的 sheet
        // 设置响应头，告知浏览器这是一个下载文件
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
        response.setContentType("application/vnd.ms-excel;charset=UTF-8"); // 设置 Excel 文件的 contentType
    }

    /**
     * 从上传的 Excel 文件中读取数据
     * <p>
     * 该方法用于从客户端上传的 Excel 文件中读取数据，并将其映射到指定的 Java 类中。
     *
     * @param file 上传的 Excel 文件
     * @param head 映射到的 Java 类类型，通常是 Excel 文件的实体类
     * @param <T>  泛型，表示读取的数据类型
     * @return 读取的数据列表
     * @throws IOException 读取 Excel 文件时可能发生的 IO 异常
     */
    public static <T> List<T> read(MultipartFile file, Class<T> head) throws IOException {
        return EasyExcel.read(file.getInputStream(), head, null)
                .autoCloseStream(false)  // 不要自动关闭流，由 Servlet 自己处理
                .doReadAllSync(); // 同步读取所有数据
    }

}
