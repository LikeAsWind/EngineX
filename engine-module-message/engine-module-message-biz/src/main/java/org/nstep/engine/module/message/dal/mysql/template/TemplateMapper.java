package org.nstep.engine.module.message.dal.mysql.template;

import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplatePageReqVO;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;

/**
 * 消息模板信息 Mapper
 *
 * @author engine
 */
@Mapper
public interface TemplateMapper extends BaseMapperX
        <TemplateDO> {

    default PageResult
            <TemplateDO> selectPage(TemplatePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX
                <TemplateDO>()
                .likeIfPresent(TemplateDO::getName, reqVO.getName())
                .eqIfPresent(TemplateDO::getMsgStatus, reqVO.getMsgStatus())
                .eqIfPresent(TemplateDO::getPushType, reqVO.getPushType())
                .eqIfPresent(TemplateDO::getCronTaskId, reqVO.getCronTaskId())
                .eqIfPresent(TemplateDO::getCronCrowdPath, reqVO.getCronCrowdPath())
                .betweenIfPresent(TemplateDO::getExpectPushTime, reqVO.getExpectPushTime())
                .eqIfPresent(TemplateDO::getSendChannel, reqVO.getSendChannel())
                .eqIfPresent(TemplateDO::getMsgContent, reqVO.getMsgContent())
                .eqIfPresent(TemplateDO::getSendAccount, reqVO.getSendAccount())
                .betweenIfPresent(TemplateDO::getCreateTime, reqVO.getCreateTime())
                .eqIfPresent(TemplateDO::getMsgType, reqVO.getMsgType())
                .eqIfPresent(TemplateDO::getAuditStatus, reqVO.getAuditStatus())
                .eqIfPresent(TemplateDO::getCurrentId, reqVO.getCurrentId())
                .orderByDesc(TemplateDO::getId));
    }

}