package org.nstep.engine.module.message.service.template;

import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplatePageReqVO;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;

/**
 * 消息模板信息 Service 接口
 *
 * @author engine
 */
public interface TemplateService {

    /**
     * 创建消息模板信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTemplate(@Valid TemplateSaveReqVO createReqVO);

    /**
     * 更新消息模板信息
     *
     * @param updateReqVO 更新信息
     */
    void updateTemplate(@Valid TemplateSaveReqVO updateReqVO);

    /**
     * 删除消息模板信息
     *
     * @param id 编号
     */
    void deleteTemplate(Long id);

    /**
     * 获得消息模板信息
     *
     * @param id 编号
     * @return 消息模板信息
     */
    TemplateDO getTemplate(Long id);

    /**
     * 获得消息模板信息分页
     *
     * @param pageReqVO   分页查询
     * @param isLoginUser 是否登陆人
     * @return 消息模板信息分页
     */
    PageResult<TemplateDO> getTemplatePage(TemplatePageReqVO pageReqVO, boolean isLoginUser);

}