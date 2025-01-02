package org.nstep.engine.framework.common.util.io;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.google.common.base.Throwables;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtils {

    /**
     * 创建临时文件
     * 该文件会在 JVM 退出时，进行删除
     *
     * @param data 文件内容
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile(String data) {
        File file = createTempFile();
        // 写入内容
        FileUtil.writeUtf8String(data, file);
        return file;
    }

    /**
     * 创建临时文件
     * 该文件会在 JVM 退出时，进行删除
     *
     * @param data 文件内容
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile(byte[] data) {
        File file = createTempFile();
        // 写入内容
        FileUtil.writeBytes(data, file);
        return file;
    }

    /**
     * 创建临时文件，无内容
     * 该文件会在 JVM 退出时，进行删除
     *
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile() {
        // 创建文件，通过 UUID 保证唯一
        File file = File.createTempFile(IdUtil.simpleUUID(), null);
        // 标记 JVM 退出时，自动删除
        file.deleteOnExit();
        return file;
    }

    /**
     * 生成文件路径
     *
     * @param content      文件内容
     * @param originalName 原始文件名
     * @return path，唯一不可重复
     */
    public static String generatePath(byte[] content, String originalName) {
        String sha256Hex = DigestUtil.sha256Hex(content);
        // 情况一：如果存在 name，则优先使用 name 的后缀
        if (StrUtil.isNotBlank(originalName)) {
            String extName = FileNameUtil.extName(originalName);
            return StrUtil.isBlank(extName) ? sha256Hex : sha256Hex + "." + extName;
        }
        // 情况二：基于 content 计算
        return sha256Hex + '.' + FileTypeUtil.getType(new ByteArrayInputStream(content));
    }

    /**
     * 读取远程链接或本地文件路径，返回File对象
     *
     * @param path         文件路径
     * @param resourcePath 远程链接或本地文件路径
     * @return File对象，如果读取失败则返回null
     */
    public static File getResourceAsFile(String path, String resourcePath) {
        InputStream urlStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = null;
            File file;

            // 判断是否为远程URL
            if (resourcePath.startsWith("http://") || resourcePath.startsWith("https://")) {
                url = new URL(resourcePath);
                file = new File(path, new File(url.getPath()).getName());

                // 如果文件不存在，则下载文件
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    urlStream = url.openStream();
                    fileOutputStream = new FileOutputStream(file);
                    IoUtil.copy(urlStream, fileOutputStream);
                }
            } else {
                file = new File(resourcePath);

                // 如果本地文件不存在，则抛出异常
                if (!file.exists()) {
                    throw new IllegalArgumentException("Local file does not exist: " + resourcePath);
                }
            }
            return file;
        } catch (Exception e) {
            log.error("FileUtils#getResourceAsFile failed: {}, resourcePath: {}", Throwables.getStackTraceAsString(e), resourcePath);
            return null; // 返回null表示失败
        } finally {
            // 确保流被关闭，避免资源泄漏
            closeQuietly(urlStream);
            closeQuietly(fileOutputStream);
        }
    }

    /**
     * 读取远程链接或本地文件路径集合，返回有效的File对象集合
     *
     * @param path       文件路径
     * @param remoteUrls cdn/oss文件访问链接集合
     * @return 有效的File对象集合
     */
    public static List<File> getRemoteUrl2File(String path, Collection<String> remoteUrls) {
        List<File> files = new ArrayList<>();
        // 遍历URL集合，获取每个URL对应的文件
        for (String remoteUrl : remoteUrls) {
            File file = getResourceAsFile(path, remoteUrl);
            if (file != null) {
                files.add(file); // 如果文件有效，加入结果集合
            }
        }
        return files;
    }

    /**
     * 安全关闭流的辅助方法，避免重复代码
     *
     * @param closeable 可关闭的资源（流）
     */
    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                log.error("Failed to close resource: {}", Throwables.getStackTraceAsString(e));
            }
        }
    }
}
