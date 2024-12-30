package org.nstep.engine.module.message.service.content;

import me.chanjar.weixin.mp.bean.template.WxMpTemplate;

import java.util.List;


public interface WeChatServiceAccountService {

    /**
     * 根据 id 查询微信模板列表
     *
     * @param id 模板 id
     * @return 模板列表
     */
    //List<WxMpTemplate> queryWxTemplateList(Long id);

    /**
     * 根据模板id查询微信模板详情
     *
     * @param id         模板id
     * @param templateId 模板id
     * @return 模板详情
     */
    //WxMpTemplate queryWxTemplateDetailByTemplateId(Integer id, String templateId);
}
