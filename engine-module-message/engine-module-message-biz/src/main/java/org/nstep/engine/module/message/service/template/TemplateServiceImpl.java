package org.nstep.engine.module.message.service.template;

import cn.hutool.core.util.ObjUtil;
import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplatePageReqVO;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dal.mysql.template.TemplateMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.nstep.engine.module.message.enums.ErrorCodeConstants.TEMPLATE_NOT_EXISTS;


/**
 * 消息模板信息 Service 实现类
 *
 * @author engine
 */
@Service
@Validated
public class TemplateServiceImpl implements TemplateService {

    @Resource
    private TemplateMapper templateMapper;

    @Override
    public Long createTemplate(TemplateSaveReqVO createReqVO) {
        // 插入
        TemplateDO template = BeanUtils.toBean(createReqVO, TemplateDO.class);
        templateMapper.insert(template);
        // 返回
        return template.getId();
    }

    @Override
    public void updateTemplate(TemplateSaveReqVO updateReqVO) {
        // 校验存在
        validateTemplateExists(updateReqVO.getId());
        // 更新
        TemplateDO updateObj = BeanUtils.toBean(updateReqVO, TemplateDO.class);
        templateMapper.updateById(updateObj);
    }

    @Override
    public void deleteTemplate(Long id) {
        // 校验存在
        validateTemplateExists(id);
        // 删除
        templateMapper.deleteById(id);
    }

    private void validateTemplateExists(Long id) {
        if (templateMapper.selectById(id) == null) {
            throw exception(TEMPLATE_NOT_EXISTS);
        }
    }

    @Override
    public TemplateDO getTemplate(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    public PageResult<TemplateDO> getTemplatePage(TemplatePageReqVO pageReqVO, boolean isLoginUser) {
        LambdaQueryWrapperX<TemplateDO> lambdaQueryWrapperX = new LambdaQueryWrapperX<TemplateDO>().likeIfPresent(TemplateDO::getName, pageReqVO.getName()).eq(TemplateDO::getMsgStatus, pageReqVO.getMsgStatus()).eq(TemplateDO::getPushType, pageReqVO.getPushType()).eq(TemplateDO::getSendChannel, pageReqVO.getSendChannel()).eq(TemplateDO::getMsgType, pageReqVO.getMsgType()).eq(TemplateDO::getAuditStatus, pageReqVO.getAuditStatus()).likeIfPresent(TemplateDO::getSendAccount, String.valueOf(ObjUtil.isEmpty(pageReqVO.getSendAccount()) ? null : pageReqVO.getSendAccount())).betweenIfPresent(TemplateDO::getCreateTime, pageReqVO.getCreateTime()).orderByDesc(TemplateDO::getUpdateTime);
        // 登陆人过滤
        if (isLoginUser) {
            lambdaQueryWrapperX.eq(TemplateDO::getCreator, SecurityFrameworkUtils.getLoginUserId());
        }
        return templateMapper.selectPage(pageReqVO, lambdaQueryWrapperX);
    }

}