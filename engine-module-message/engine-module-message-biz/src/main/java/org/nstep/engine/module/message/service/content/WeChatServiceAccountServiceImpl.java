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


/**
 * 微信服务账号服务实现类
 * <p>
 * 该类实现了 WeChatServiceAccountService 接口，提供了查询微信模板列表和查询微信模板详情的功能。
 * 它通过调用微信 SDK 获取模板信息，并处理相关的异常情况。
 */
@Service
@Slf4j
public class WeChatServiceAccountServiceImpl implements WeChatServiceAccountService {

    @Resource
    private AccountUtil accountUtil; // 注入 AccountUtil，用于获取微信服务账号

    /**
     * 根据 id 查询微信模板列表
     * <p>
     * 该方法根据提供的 id 查询微信模板列表。它通过 AccountUtil 获取微信服务账号的实例，
     * 并调用微信 SDK 获取所有私人模板列表。如果发生异常，抛出模板信息获取失败的异常。
     *
     * @param id 模板 id，用于查询对应的微信模板列表
     * @return 返回微信模板列表，包含多个 WxMpTemplate 对象
     */
    @Override
    public List<WxMpTemplate> queryWxTemplateList(Integer id) {
        // 获取微信服务账号实例
        WxMpService wxMpService = accountUtil.getAccount(id, WxMpService.class);
        List<WxMpTemplate> allPrivateTemplate;
        try {
            // 获取所有私人模板
            allPrivateTemplate = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
        } catch (Exception e) {
            // 捕获异常并抛出自定义异常
            throw exception(ErrorCodeConstants.TEMPLATE_INFO_FETCH_FAILED);
        }
        return allPrivateTemplate; // 返回模板列表
    }

    /**
     * 根据模板id查询微信模板详情
     * <p>
     * 该方法根据模板 ID 和具体的模板模板ID查询微信模板的详细信息，返回一个包含详细模板信息的 WxMpTemplate 对象。
     * 如果未找到对应模板，返回 null。
     *
     * @param id         模板id，用于查询对应的微信模板
     * @param templateId 模板的唯一标识符，用于查询特定模板的详情
     * @return 返回查询到的微信模板详情，包含详细的模板信息，如果未找到模板则返回 null
     */
    @Override
    public WxMpTemplate queryWxTemplateDetailByTemplateId(Integer id, String templateId) {
        // 获取微信服务账号实例
        WxMpService wxMpService = accountUtil.getAccount(id, WxMpService.class);
        List<WxMpTemplate> allPrivateTemplate;
        try {
            // 获取所有私人模板
            allPrivateTemplate = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
            // 根据模板ID查找对应的模板
            Optional<WxMpTemplate> wxMpTemplateOptional = allPrivateTemplate.stream()
                    .filter(template -> templateId.equals(template.getTemplateId())).findFirst();
            return wxMpTemplateOptional.orElse(null); // 如果找不到模板，返回 null
        } catch (Exception e) {
            // 捕获异常并抛出自定义异常
            throw exception(ErrorCodeConstants.TEMPLATE_INFO_FETCH_FAILED);
        }
    }
}
