package org.nstep.engine.framework.xxljob.constant;

/**
 * @author yangzhitong
 * <p>
 * 该类包含与XXL Job调度系统交互的常量。
 * 它包括用于作业管理操作的URL和与作业组类型相关的常量。
 */
public class XxlJobDataConstants {

    /**
     * 执行器地址注册类型 - 自动注册
     * 该常量表示执行器地址的注册类型为“自动注册”。
     * 使用此类型的执行器将自动注册到系统中。
     */
    public static final Integer XXL_GROUP_TYPE_AUTO = 0;

    // 作业管理操作的URL常量

    /**
     * 添加作业的URL。
     * 该URL用于向XXL Job调度系统发送请求，添加一个新的作业。
     */
    public static final String ADD_URL = "/jobinfo/add";

    /**
     * 更新作业的URL。
     * 该URL用于向XXL Job调度系统发送请求，更新现有的作业。
     */
    public static final String UPDATE_URL = "/jobinfo/update";

    /**
     * 删除作业的URL。
     * 该URL用于向XXL Job调度系统发送请求，删除一个作业。
     */
    public static final String REMOVE_URL = "/jobinfo/remove";

    /**
     * 停止作业的URL。
     * 该URL用于向XXL Job调度系统发送请求，停止正在运行的作业。
     */
    public static final String STOP_URL = "/jobinfo/stop";

    /**
     * 启动作业的URL。
     * 该URL用于向XXL Job调度系统发送请求，启动已停止的作业。
     */
    public static final String START_URL = "/jobinfo/start";

    /**
     * 获取作业组ID的URL。
     * 该URL用于向XXL Job调度系统发送请求，获取作业组的ID。
     */
    public static final String GET_GROUP_ID = "/jobgroup/pageList";

    /**
     * 保存作业组的URL。
     * 该URL用于向XXL Job调度系统发送请求，保存一个新的作业组。
     */
    public static final String SAVE_GROUP_URL = "/jobgroup/save";
}
