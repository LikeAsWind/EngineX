package org.nstep.engine.module.infra.framework.file.core.utils;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TransmittableThreadLocal;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.apache.tika.Tika;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * 文件类型 Utils
 * 提供文件类型（MIME 类型）相关的工具方法，包括通过字节数组或文件名来获取文件类型，
 * 以及将文件内容写入 HTTP 响应作为附件。
 */
public class FileTypeUtils {

    // 使用 TransmittableThreadLocal 来确保 Tika 实例在多线程环境下的传递
    private static final ThreadLocal<Tika> TIKA = TransmittableThreadLocal.withInitial(Tika::new);

    /**
     * 获取文件的 MIME 类型（基于文件内容）。
     * 对于某些文件类型（如 doc、jar 等），可能会有误差。
     *
     * @param data 文件内容
     * @return MIME 类型，如果无法识别则返回 "application/octet-stream"
     */
    @SneakyThrows
    public static String getMineType(byte[] data) {
        return TIKA.get().detect(data);
    }

    /**
     * 根据文件名获取文件的 MIME 类型。
     * 在某些情况下，基于文件名获取 MIME 类型比基于文件内容更准确，
     * 例如对于 JAR 文件，通过文件名识别更为准确。
     *
     * @param name 文件名
     * @return MIME 类型，如果无法识别则返回 "application/octet-stream"
     */
    public static String getMineType(String name) {
        return TIKA.get().detect(name);
    }

    /**
     * 同时使用文件内容和文件名来获取文件的 MIME 类型。
     * 这是最准确的方法，尤其在文件内容和文件名都有的情况下。
     *
     * @param data 文件内容
     * @param name 文件名
     * @return MIME 类型，如果无法识别则返回 "application/octet-stream"
     */
    public static String getMineType(byte[] data, String name) {
        return TIKA.get().detect(data, name);
    }

    /**
     * 将文件内容写入 HTTP 响应作为附件。
     * 会根据文件内容类型设置响应头，并处理视频文件的兼容性问题。
     *
     * @param response 响应对象
     * @param filename 文件名
     * @param content  文件内容
     * @throws IOException 如果写入文件内容时发生 I/O 错误
     */
    public static void writeAttachment(HttpServletResponse response, String filename, byte[] content) throws IOException {
        // 设置 Content-Disposition 响应头，提示浏览器下载文件
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(filename, "UTF-8"));

        // 获取文件的 MIME 类型
        String contentType = getMineType(content, filename);
        response.setContentType(contentType);

        // 针对视频文件的特殊处理，解决视频在移动端播放的兼容性问题
        if (StrUtil.containsIgnoreCase(contentType, "video")) {
            response.setHeader("Content-Length", String.valueOf(content.length - 1));
            response.setHeader("Content-Range", String.valueOf(content.length - 1));
            response.setHeader("Accept-Ranges", "bytes");
        }

        // 输出文件内容到响应流
        IoUtil.write(response.getOutputStream(), false, content);
    }

}
