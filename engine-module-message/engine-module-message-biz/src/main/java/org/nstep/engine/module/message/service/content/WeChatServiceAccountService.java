package org.nstep.engine.module.message.service.content;

import me.chanjar.weixin.mp.bean.template.WxMpTemplate;

import java.util.List;

/**
 * 微信服务账号服务接口
 * <p>
 * 该接口定义了与微信服务账号相关的操作，主要包括查询微信模板列表和根据模板ID查询微信模板详情等功能。
 * 通过该接口，用户可以访问和操作微信模板信息。
 */
public interface WeChatServiceAccountService {

    /**
     * 根据 id 查询微信模板列表
     * <p>
     * 该方法根据提供的模板 ID 查询微信模板列表，返回一个包含模板信息的列表。
     *
     * @param id 模板 id，用于查询对应的微信模板列表
     * @return 返回微信模板列表，列表包含多个 WxMpTemplate 对象
     */
    List<WxMpTemplate> queryWxTemplateList(Integer id);

    /**
     * 根据模板id查询微信模板详情
     * <p>
     * 该方法根据模板 ID 和具体的模板模板ID查询微信模板的详细信息，返回一个包含详细模板信息的 WxMpTemplate 对象。
     *
     * @param id         模板id，用于查询对应的微信模板
     * @param templateId 模板的唯一标识符，用于查询特定模板的详情
     * @return 返回查询到的微信模板详情，包含详细的模板信息
     */
    WxMpTemplate queryWxTemplateDetailByTemplateId(Integer id, String templateId);
}
