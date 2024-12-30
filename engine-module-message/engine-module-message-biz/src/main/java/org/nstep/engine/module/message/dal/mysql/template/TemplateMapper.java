package org.nstep.engine.module.message.dal.mysql.template;

import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplatePageReqVO;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;

/**
 * 消息模板信息 Mapper
 * <p>
 * 该接口用于操作与消息模板相关的数据，包括查询、分页查询等操作。继承自 BaseMapperX，提供基本的数据库操作方法。
 * </p>
 *
 * @author engine
 */
@Mapper
public interface TemplateMapper extends BaseMapperX<TemplateDO> {

    /**
     * 分页查询消息模板信息
     * <p>
     * 该方法根据传入的请求对象（TemplatePageReqVO）进行分页查询，并根据提供的条件进行过滤。
     * 查询条件包括标题、消息状态、推送类型、定时任务ID、发送渠道等字段。结果按 ID 降序排列。
     * </p>
     *
     * @param reqVO 分页请求对象，包含查询条件
     * @return 返回分页结果，包含匹配条件的消息模板信息
     */
    default PageResult<TemplateDO> selectPage(TemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TemplateDO>()
                // 如果标题字段不为空，则进行模糊查询
                .likeIfPresent(TemplateDO::getName, reqVO.getName())
                // 如果消息状态字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getMsgStatus, reqVO.getMsgStatus())
                // 如果推送类型字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getPushType, reqVO.getPushType())
                // 如果定时任务ID字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getCronTaskId, reqVO.getCronTaskId())
                // 如果定时发送人群路径字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getCronCrowdPath, reqVO.getCronCrowdPath())
                // 如果期望发送时间字段不为空，则进行时间范围查询
                .betweenIfPresent(TemplateDO::getExpectPushTime, reqVO.getExpectPushTime())
                // 如果发送渠道字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getSendChannel, reqVO.getSendChannel())
                // 如果消息内容字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getMsgContent, reqVO.getMsgContent())
                // 如果发送账号字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getSendAccount, reqVO.getSendAccount())
                // 如果创建时间字段不为空，则进行时间范围查询
                .betweenIfPresent(TemplateDO::getCreateTime, reqVO.getCreateTime())
                // 如果消息类型字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getMsgType, reqVO.getMsgType())
                // 如果审核状态字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getAuditStatus, reqVO.getAuditStatus())
                // 如果当前定时模板使用用户ID字段不为空，则进行精确匹配
                .eqIfPresent(TemplateDO::getCurrentId, reqVO.getCurrentId())
                // 按 ID 降序排列结果
                .orderByDesc(TemplateDO::getId));
    }

}
