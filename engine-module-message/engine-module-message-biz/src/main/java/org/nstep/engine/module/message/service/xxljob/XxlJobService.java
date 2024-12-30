package org.nstep.engine.module.message.service.xxljob;


import org.nstep.engine.framework.xxljob.domain.XxlJobInfo;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;

/**
 * XxlJobService 接口定义了与XXL Job调度系统交互的相关操作。
 * 包括保存、启动、停止、更新和删除定时任务的方法。
 * 该接口用于管理和操作XXL Job的定时任务。
 */
public interface XxlJobService {

    /**
     * 保存定时任务模板信息。
     *
     * @param messageTemplate 任务模板对象
     * @return 任务ID
     */
    Integer save(TemplateDO messageTemplate);

    /**
     * 根据任务模板构建XXL Job任务信息。
     *
     * @param messageTemplate 任务模板对象
     * @return 构建的XXL Job任务信息
     */
    XxlJobInfo buildXxlJobInfo(TemplateDO messageTemplate);

    /**
     * 启动定时任务。
     *
     * @param id 任务ID
     */
    void start(Long id);

    /**
     * 停止定时任务。
     *
     * @param id 任务ID
     */
    void stop(Long id);

    /**
     * 更新定时任务。
     *
     * @param messageTemplate 任务模板对象
     */
    void update(TemplateDO messageTemplate);

    /**
     * 删除定时任务。
     *
     * @param id 任务ID
     */
    void remove(Long id);
}
