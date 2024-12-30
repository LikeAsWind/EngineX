package org.nstep.engine.module.message.service.template;

import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplatePageReqVO;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateRespVO;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;

import java.util.List;

/**
 * 消息模板信息 Service 接口
 * <p>
 * 该接口定义了消息模板相关的业务逻辑方法，包括创建、更新、删除、查询单个模板、分页查询模板等功能。
 * 实现类需要提供这些方法的具体实现。
 */
public interface TemplateService {

    /**
     * 创建消息模板信息
     * <p>
     * 该方法接收一个包含消息模板信息的请求对象（createReqVO），并将其保存为一个新的消息模板。
     * 返回新创建的模板的编号（id）。
     *
     * @param createReqVO 创建信息，包含模板的各项属性
     * @return 新创建的消息模板的编号
     */
    Long createTemplate(@Valid TemplateSaveReqVO createReqVO);

    /**
     * 更新消息模板信息
     * <p>
     * 该方法接收一个包含更新信息的请求对象（updateReqVO），并根据该信息更新已存在的消息模板。
     *
     * @param updateReqVO 更新信息，包含模板的各项更新属性
     */
    void updateTemplate(@Valid TemplateSaveReqVO updateReqVO);

    /**
     * 删除消息模板信息
     * <p>
     * 该方法接收一个模板编号（id），并删除对应的消息模板。
     *
     * @param id 模板的编号
     */
    void deleteTemplate(Long id);

    /**
     * 删除消息模板信息
     * <p>
     * 该方法接收一个模板编号（id），并删除对应的消息模板。
     *
     * @param ids 模板的编号数组
     */
    void deleteTemplate(Long[] ids);

    /**
     * 获得消息模板信息
     * <p>
     * 该方法根据提供的模板编号（id），返回对应的消息模板信息。
     *
     * @param id 模板的编号
     * @return 返回对应的消息模板信息
     */
    TemplateDO getTemplate(Long id);

    /**
     * 获得消息模板信息分页
     * <p>
     * 该方法根据分页请求参数（pageReqVO）和是否为当前登录用户（isLoginUser），返回分页的消息模板信息。
     *
     * @param pageReqVO   分页查询请求对象，包含分页条件
     * @param isLoginUser 是否为当前登录用户，决定查询的模板是否与当前用户相关
     * @return 返回分页后的消息模板信息
     */
    PageResult<TemplateDO> getTemplatePage(TemplatePageReqVO pageReqVO, boolean isLoginUser);

    /**
     * 更新模板的审核状态
     * <p>
     * 该方法用于修改指定消息模板的审核状态，例如审核通过或未通过。
     *
     * @param id     模板的唯一标识 ID
     * @param status 新的审核状态值
     * @return 返回布尔值，表示操作是否成功
     */
    Boolean updateAudit(Long id, Integer status);

    /**
     * 获取当前用户的消息模板列表
     * <p>
     * 该方法用于查询当前登录用户所拥有的消息模板列表。
     *
     * @return 包含模板信息的 {@code List<TemplateRespVO>} 对象
     */
    List<TemplateRespVO> list4CurrUser();


}
