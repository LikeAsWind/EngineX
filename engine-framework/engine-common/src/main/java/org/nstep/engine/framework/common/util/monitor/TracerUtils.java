package org.nstep.engine.framework.common.util.monitor;

import org.apache.skywalking.apm.toolkit.trace.TraceContext;

/**
 * 链路追踪工具类
 * <p>
 * 该工具类用于获取链路追踪编号，主要依赖于 SkyWalking 框架提供的 TraceContext。
 * 考虑到每个 starter 都需要用到该工具类，因此将其放置在 common 模块的 util 包下，以便共享使用。
 * <p>
 * 链路追踪用于分布式系统中追踪请求的流转路径，帮助开发者定位问题、分析性能瓶颈等。
 */
public class TracerUtils {

    /**
     * 私有化构造方法
     * <p>
     * 该工具类不需要实例化，因此将构造方法私有化，避免外部创建实例。
     */
    private TracerUtils() {
    }

    /**
     * 获得链路追踪编号，直接返回 SkyWalking 的 TraceId。
     * 如果当前请求没有 TraceId（即不存在链路追踪信息），则返回空字符串。
     *
     * @return 链路追踪编号，若没有链路追踪信息，则返回空字符串。
     */
    public static String getTraceId() {
        return TraceContext.traceId();
    }

}
