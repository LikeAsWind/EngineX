package org.nstep.engine.framework.ip.core.utils;

import cn.hutool.core.io.resource.ResourceUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Searcher;
import org.nstep.engine.framework.ip.core.Area;

import java.io.IOException;

/**
 * IP 工具类
 * <p>
 * IP 数据源来自 ip2region.xdb 精简版，基于 <a href="https://gitee.com/zhijiantianya/ip2region"/> 项目
 */
@Slf4j
public class IPUtils {

    /**
     * 初始化 SEARCHER
     */
    @SuppressWarnings("InstantiationOfUtilityClass")
    private final static IPUtils INSTANCE = new IPUtils();

    /**
     * IP 查询器，启动加载到内存中
     */
    private static Searcher SEARCHER;

    /**
     * 私有化构造
     */
    private IPUtils() {
        try {
            long now = System.currentTimeMillis();
            byte[] bytes = ResourceUtil.readBytes("ip2region.xdb");
            SEARCHER = Searcher.newWithBuffer(bytes);
            log.info("启动加载 IPUtils 成功，耗时 ({}) 毫秒", System.currentTimeMillis() - now);
        } catch (IOException e) {
            log.error("启动加载 IPUtils 失败", e);
        }
    }

    /**
     * 查询 IP 对应的地区编号
     *
     * @param ip IP 地址，格式为 127.0.0.1
     * @return 地区id
     */
    @SneakyThrows
    public static Integer getAreaId(String ip) {
        return Integer.parseInt(SEARCHER.search(ip.trim()));
    }

    /**
     * 查询 IP 对应的地区编号
     *
     * @param ip IP 地址的时间戳，格式参考{@link Searcher#checkIP(String)} 的返回
     * @return 地区编号
     */
    @SneakyThrows
    public static Integer getAreaId(long ip) {
        return Integer.parseInt(SEARCHER.search(ip));
    }

    /**
     * 查询 IP 对应的地区
     *
     * @param ip IP 地址，格式为 127.0.0.1
     * @return 地区
     */
    public static Area getArea(String ip) {
        return AreaUtils.getArea(getAreaId(ip));
    }

    /**
     * 查询 IP 对应的地区
     *
     * @param ip IP 地址的时间戳，格式参考{@link Searcher#checkIP(String)} 的返回
     * @return 地区
     */
    public static Area getArea(long ip) {
        return AreaUtils.getArea(getAreaId(ip));
    }


    /**
     * 脱敏字符串中的IP地址
     *
     * @param url 原始JDBC URL
     * @return 脱敏后的JDBC URL
     */
    public static String maskIpAddress(String url) {
        // 正则表达式匹配IP地址
        String regex = "(//)(\\d+\\.\\d+\\.\\d+\\.\\d+)";

        // 使用正则表达式替换IP地址为脱敏的格式
        return url.replaceAll(regex, "$1xxx.xxx.xxx.xxx");
    }
}
