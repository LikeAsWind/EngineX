package org.nstep.engine.module.message.service.content;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.nstep.engine.module.message.util.AccountUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;


@Service
@Slf4j
public class WeChatServiceAccountServiceImpl implements WeChatServiceAccountService {

    @Resource
    private AccountUtil accountUtil;

    /**
     * 根据 id 查询微信模板列表
     *
     * @param id 模板 id
     * @return 模板列表
     */
    /*@Override
    public List<WxMpTemplate> queryWxTemplateList(Long id) {
        WxMpService wxMpService = accountUtil.getAccount(id, WxMpService.class);
        List<WxMpTemplate> allPrivateTemplate;
        try {
            allPrivateTemplate = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
        } catch (Exception e) {
            throw exception(ErrorCodeConstants.TEMPLATE_INFO_FETCH_FAILED);
        }
        return allPrivateTemplate;
    }

    *//**
     * 根据模板id查询微信模板详情
     *
     * @param id         模板id
     * @param templateId 模板id
     * @return 模板详情
     *//*
    @Override
    public WxMpTemplate queryWxTemplateDetailByTemplateId(Integer id, String templateId) {
        WxMpService wxMpService = accountUtil.getAccount(id, WxMpService.class);
        List<WxMpTemplate> allPrivateTemplate;
        try {
            allPrivateTemplate = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
            Optional<WxMpTemplate> wxMpTemplateOptional = allPrivateTemplate.stream()
                    .filter(template -> templateId.equals(template.getTemplateId())).findFirst();
            return wxMpTemplateOptional.orElse(null);
        } catch (Exception e) {
            throw exception(ErrorCodeConstants.TEMPLATE_INFO_FETCH_FAILED);
        }
    }*/
}
