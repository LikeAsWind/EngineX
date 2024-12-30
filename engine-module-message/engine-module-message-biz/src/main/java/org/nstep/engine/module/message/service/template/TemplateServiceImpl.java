package org.nstep.engine.module.message.service.template;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.module.message.constant.DDingDingSendMessageTypeConstants;
import org.nstep.engine.module.message.constant.GeTuiConstants;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.constant.WeChatConstants;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplatePageReqVO;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dal.mysql.template.TemplateMapper;
import org.nstep.engine.module.message.domain.dingding.DingDingRobotParam;
import org.nstep.engine.module.message.domain.weChat.EnterpriseWeChatRobotParam;
import org.nstep.engine.module.message.dto.*;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * 消息模板信息 Service 实现类
 * 该类实现了消息模板的增删改查操作，并包含了对不同消息渠道的校验逻辑
 */
@Service
@Validated
public class TemplateServiceImpl implements TemplateService {

    @Resource
    private TemplateMapper templateMapper;

    /**
     * 创建消息模板
     *
     * @param createReqVO 创建请求的模板信息
     * @return Long 创建成功后返回模板的ID
     */
    @Override
    public Long createTemplate(TemplateSaveReqVO createReqVO) {
        boolean isTrue = checkCreateReqVO(createReqVO);
        // 校验请求的有效性
        if (!isTrue) {
            throw exception(ErrorCodeConstants.INVALID_FIELDS); // 如果校验不通过，抛出异常
        }
        if (Objects.equals(createReqVO.getPushType(), MessageDataConstants.TIMING)) {
            // 如果是定时推送类型，设置消息状态为停止
            createReqVO.setMsgStatus(MessageDataConstants.MSG_STOP);
        }
        // 将请求数据转换为模板对象
        TemplateDO template = BeanUtils.toBean(createReqVO, TemplateDO.class);
        templateMapper.insert(template); // 插入数据库
        // 返回插入后的模板ID
        return template.getId();
    }

    /**
     * 校验创建请求的有效性
     *
     * @param messageTemplate 创建请求的模板信息
     * @return boolean 校验结果
     */
    private boolean checkCreateReqVO(TemplateSaveReqVO messageTemplate) {
        if (messageTemplate.getSendAccount() == null || messageTemplate.getSendAccount() == 0) {
            return false; // 发送账户为空或无效
        }
        if (messageTemplate.getSendChannel() == null || messageTemplate.getSendChannel() == 0) {
            return false; // 发送渠道为空或无效
        }
        // 钉钉机器人、微信服务号等不同渠道的预校验
        // 校验通过
        return !dingDongRobotPreCheck(messageTemplate) &&
                !weChatServiceAccountPreCheck(messageTemplate) &&
                !pushPreCheck(messageTemplate) &&
                !feiShuRobotPreCheck(messageTemplate) &&
                !enterpriseWeChatRobotPreCheck(messageTemplate); // 如果任意校验失败，返回false

    }

    /**
     * 钉钉机器人后端校验
     *
     * @param messageTemplate 创建请求的模板信息
     * @return true 需要拦截，false 放行
     */
    public boolean dingDongRobotPreCheck(TemplateSaveReqVO messageTemplate) {
        Integer sendChannel = messageTemplate.getSendChannel();
        if (!MessageDataConstants.DING_DING_ROBOT.equals(sendChannel)) {
            return false; // 如果不是钉钉机器人，放行
        }
        DingDingRobotContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), DingDingRobotContentModel.class);
        // 根据消息类型进行不同的校验
        if (StrUtil.isBlank(contentModel.getSendType())) {
            return true;
        }
        // 校验不同类型的消息内容（文本、链接、markdown等）
        return switch (contentModel.getSendType()) {
            case DDingDingSendMessageTypeConstants.TEXT -> {
                DingDingRobotParam.Text text = JSON.parseObject(contentModel.getContent(), DingDingRobotParam.Text.class);
                yield StrUtil.isBlank(text.getContent());
            }
            case DDingDingSendMessageTypeConstants.LINK -> {
                DingDingRobotParam.Link link = JSON.parseObject(contentModel.getContent(), DingDingRobotParam.Link.class);
                yield StrUtil.isBlank(link.getTitle()) || StrUtil.isBlank(link.getText()) || StrUtil.isBlank(link.getMessageUrl()) || StrUtil.isBlank(link.getPicUrl());
            }
            case DDingDingSendMessageTypeConstants.MARKDOWN -> {
                DingDingRobotParam.Markdown markdown = JSON.parseObject(contentModel.getContent(), DingDingRobotParam.Markdown.class);
                yield StrUtil.isBlank(markdown.getTitle()) || StrUtil.isBlank(markdown.getText());
            }
            case DDingDingSendMessageTypeConstants.ACTION_CARD -> {
                DingDingRobotParam.ActionCard actionCard = JSON.parseObject(contentModel.getContent(), DingDingRobotParam.ActionCard.class);
                yield StrUtil.isBlank(actionCard.getTitle()) || StrUtil.isBlank(actionCard.getText()) || CollectionUtil.isEmpty(actionCard.getBtns()) || StrUtil.isBlank(actionCard.getBtnOrientation());
            }
            case DDingDingSendMessageTypeConstants.FEED_CARD -> {
                DingDingRobotParam.FeedCard feedCard = JSON.parseObject(contentModel.getContent(), DingDingRobotParam.FeedCard.class);
                yield CollectionUtil.isEmpty(feedCard.getLinks());
            }
            default -> false;
        };
    }

    /**
     * APP通知栏后端校验
     *
     * @param messageTemplate 创建请求的模板信息
     * @return true 需要拦截，false 放行
     */
    public boolean pushPreCheck(TemplateSaveReqVO messageTemplate) {
        Integer sendChannel = messageTemplate.getSendChannel();
        if (!MessageDataConstants.PUSH.equals(sendChannel)) {
            return false; // 如果不是APP通知栏，放行
        }
        PushContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), PushContentModel.class);
        // 校验点击类型的有效性
        String clickType = contentModel.getClickType();
        if (StrUtil.isBlank(contentModel.getChannelLevel())) {
            return true; // 如果频道等级为空，拦截
        }
        // 校验不同点击类型的内容
        return switch (clickType) {
            case GeTuiConstants.CLICK_TYPE_URL -> StrUtil.isBlank(contentModel.getUrl());
            case GeTuiConstants.CLICK_TYPE_INTENT -> StrUtil.isBlank(contentModel.getIntent());
            case GeTuiConstants.CLICK_TYPE_PAYLOAD, GeTuiConstants.CLICK_TYPE_PAYLOAD_CUSTOM ->
                    StrUtil.isBlank(contentModel.getPayload());
            default -> false;
        };
    }

    /**
     * 微信服务号后端校验
     *
     * @param messageTemplate 创建请求的模板信息
     * @return true 需要拦截，false 放行
     */
    public boolean weChatServiceAccountPreCheck(TemplateSaveReqVO messageTemplate) {
        Integer sendChannel = messageTemplate.getSendChannel();
        if (!MessageDataConstants.WECHAT_SERVICE_ACCOUNT.equals(sendChannel)) {
            return false; // 如果不是微信服务号，放行
        }
        WeChatServiceAccountContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), WeChatServiceAccountContentModel.class);
        // 校验模板ID和链接的有效性
        return StrUtil.isBlank(contentModel.getTemplateId()) || StrUtil.isBlank(contentModel.getUrl()) || contentModel.getLinkType() == null || contentModel.getLinkType() == 0;
    }

    /**
     * 飞书机器人后端校验
     *
     * @param messageTemplate 创建请求的模板信息
     * @return true 需要拦截，false 放行
     */
    public boolean feiShuRobotPreCheck(TemplateSaveReqVO messageTemplate) {
        Integer sendChannel = messageTemplate.getSendChannel();
        if (!MessageDataConstants.FEI_SHU_ROBOT.equals(sendChannel)) {
            return false; // 如果不是飞书机器人，放行
        }
        FeiShuRobotContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), FeiShuRobotContentModel.class);
        // 校验消息类型
        if (StrUtil.isBlank(contentModel.getMsgType())) {
            return true;
        }
        // 校验文本消息内容
        if (DDingDingSendMessageTypeConstants.TEXT.equals(contentModel.getMsgType())) {
            return StrUtil.isBlank(contentModel.getText().getContent());
        }
        return false;
    }

    /**
     * 企业微信机器人后端校验
     *
     * @param messageTemplate 创建请求的模板信息
     * @return true 需要拦截，false 放行
     */
    public boolean enterpriseWeChatRobotPreCheck(TemplateSaveReqVO messageTemplate) {
        Integer sendChannel = messageTemplate.getSendChannel();
        if (!MessageDataConstants.ENTERPRISE_WECHAT_ROBOT.equals(sendChannel)) {
            return false; // 如果不是企业微信机器人，放行
        }
        EnterpriseWeChatRobotContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), EnterpriseWeChatRobotContentModel.class);
        // 校验不同消息类型的内容
        if (StrUtil.isBlank(contentModel.getSendType())) {
            return true;
        }
        JSONObject jsonObject = JSON.parseObject(contentModel.getContent());
        return switch (contentModel.getSendType()) {
            case WeChatConstants.TEXT -> {
                EnterpriseWeChatRobotParam.TextDTO textDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.TextDTO.class);
                yield StrUtil.isBlank(textDTO.getContent());
            }
            case WeChatConstants.MARKDOWN -> {
                EnterpriseWeChatRobotParam.MarkdownDTO markdownDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.MarkdownDTO.class);
                yield StrUtil.isBlank(markdownDTO.getContent());
            }
            case WeChatConstants.IMAGE -> {
                EnterpriseWeChatRobotParam.ImageDTO imageDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.ImageDTO.class);
                yield StrUtil.isBlank(imageDTO.getBase64()) || StrUtil.isBlank(imageDTO.getMd5());
            }
            case WeChatConstants.NEWS -> {
                EnterpriseWeChatRobotParam.NewsDTO newsDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.NewsDTO.class);
                yield CollectionUtil.isEmpty(newsDTO.getArticles());
            }
            case WeChatConstants.FILE -> {
                EnterpriseWeChatRobotParam.FileDTO fileDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.FileDTO.class);
                yield StrUtil.isBlank(fileDTO.getMedia_id());
            }
            case WeChatConstants.VOICE -> {
                EnterpriseWeChatRobotParam.VoiceDTO voiceDTO = jsonObject.toJavaObject(EnterpriseWeChatRobotParam.VoiceDTO.class);
                yield StrUtil.isBlank(voiceDTO.getMedia_id());
            }
            default -> false;
        };
    }

    /**
     * 更新消息模板
     *
     * @param updateReqVO 更新请求的模板信息
     */
    @Override
    public void updateTemplate(TemplateSaveReqVO updateReqVO) {
        // 校验模板是否存在
        validateTemplateExists(updateReqVO.getId());
        // 校验创建请求的有效性
        boolean isTrue = checkCreateReqVO(updateReqVO);
        // 校验请求的有效性
        if (!isTrue) {
            throw exception(ErrorCodeConstants.INVALID_FIELDS); // 如果校验不通过，抛出异常
        }
        // 更新模板
        TemplateDO updateObj = BeanUtils.toBean(updateReqVO, TemplateDO.class);
        templateMapper.updateById(updateObj);
    }

    /**
     * 删除消息模板
     *
     * @param id 模板ID
     */
    @Override
    public void deleteTemplate(Long id) {
        // 校验模板是否存在
        validateTemplateExists(id);
        // 删除模板
        templateMapper.deleteById(id);
    }

    /**
     * 校验模板是否存在
     *
     * @param id 模板ID
     */
    private void validateTemplateExists(Long id) {
        if (templateMapper.selectById(id) == null) {
            throw exception(ErrorCodeConstants.TEMPLATE_NOT_EXISTS); // 如果模板不存在，抛出异常
        }
    }

    /**
     * 获取消息模板
     *
     * @param id 模板ID
     * @return TemplateDO 模板对象
     */
    @Override
    public TemplateDO getTemplate(Long id) {
        return templateMapper.selectById(id);
    }

    /**
     * 获取消息模板分页列表
     *
     * @param pageReqVO   分页请求参数
     * @param isLoginUser 是否是登录用户
     * @return PageResult<TemplateDO> 分页结果
     */
    @Override
    public PageResult<TemplateDO> getTemplatePage(TemplatePageReqVO pageReqVO, boolean isLoginUser) {
        // 构建查询条件
        LambdaQueryWrapperX<TemplateDO> lambdaQueryWrapperX = new LambdaQueryWrapperX<TemplateDO>()
                .likeIfPresent(TemplateDO::getName, pageReqVO.getName())
                .eq(TemplateDO::getMsgStatus, pageReqVO.getMsgStatus())
                .eq(TemplateDO::getPushType, pageReqVO.getPushType())
                .eq(TemplateDO::getSendChannel, pageReqVO.getSendChannel())
                .eq(TemplateDO::getMsgType, pageReqVO.getMsgType())
                .eq(TemplateDO::getAuditStatus, pageReqVO.getAuditStatus())
                .likeIfPresent(TemplateDO::getSendAccount, String.valueOf(ObjUtil.isEmpty(pageReqVO.getSendAccount()) ? null : pageReqVO.getSendAccount()))
                .betweenIfPresent(TemplateDO::getCreateTime, pageReqVO.getCreateTime())
                .orderByDesc(TemplateDO::getUpdateTime);
        // 如果是登录用户，增加登录用户过滤
        if (isLoginUser) {
            lambdaQueryWrapperX.eq(TemplateDO::getCreator, SecurityFrameworkUtils.getLoginUserId());
        }
        return templateMapper.selectPage(pageReqVO, lambdaQueryWrapperX);
    }
}
