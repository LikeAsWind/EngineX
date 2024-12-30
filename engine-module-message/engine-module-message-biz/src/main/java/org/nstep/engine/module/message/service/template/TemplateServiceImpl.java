package org.nstep.engine.module.message.service.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.framework.mybatis.core.query.QueryWrapperX;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.module.message.constant.DDingDingSendMessageTypeConstants;
import org.nstep.engine.module.message.constant.GeTuiConstants;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.constant.WeChatConstants;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplatePageReqVO;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateRespVO;
import org.nstep.engine.module.message.controller.admin.template.vo.TemplateSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dal.mysql.template.TemplateMapper;
import org.nstep.engine.module.message.domain.dingding.DingDingRobotParam;
import org.nstep.engine.module.message.domain.weChat.EnterpriseWeChatRobotParam;
import org.nstep.engine.module.message.dto.content.*;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.nstep.engine.module.message.service.xxljob.XxlJobService;
import org.nstep.engine.module.message.util.RedisKeyUtil;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Arrays;
import java.util.List;
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

    @Resource
    private XxlJobService xxlJobService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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
     * 删除消息模板
     * <p>
     * 该方法用于删除指定的消息模板。如果模板包含定时任务ID，首先会删除XXL调度中心中的定时任务。
     * 如果模板包含定时任务的群体路径，则会删除相关文件。此外，如果模板属于定时任务类型，还会删除定时任务的链路追踪记录。
     *
     * @param ids 模板ID数组，包含需要删除的多个模板ID
     */
    @Override
    public void deleteTemplate(Long[] ids) {
        // 根据模板ID数组查询对应的模板信息
        List<TemplateDO> messageTemplates = templateMapper.selectByIds(Arrays.asList(ids));

        // 遍历每个模板
        for (TemplateDO messageTemplate : messageTemplates) {
            // 如果模板的定时任务ID不为空，说明该模板已经在XXL调度中心注册过定时任务
            if (Objects.nonNull(messageTemplate.getCronTaskId())) {
                // 删除XXL调度中心中的定时任务
                xxlJobService.remove(messageTemplate.getCronTaskId());
            }

            // 如果模板的定时任务群体路径不为空，说明该模板包含定时任务的群体文件
            if (Objects.nonNull(messageTemplate.getCronCrowdPath())) {
                // 删除与定时任务群体相关的文件
                FileUtil.del(messageTemplate.getCronCrowdPath());
            }

            // 如果模板的推送类型为定时任务类型
            if (messageTemplate.getPushType().equals(MessageDataConstants.TIMING)) {
                // 删除定时模板的链路追踪记录
                stringRedisTemplate.delete(RedisKeyUtil.getCronTaskCordsRedisKey(SecurityFrameworkUtils.getLoginUserId(), messageTemplate.getId().toString()));
            }
        }
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

    /**
     * 更新模板的审核状态
     * <p>
     * 该方法用于修改指定消息模板的审核状态，例如审核通过或未通过。
     *
     * @param id     模板的唯一标识 ID
     * @param status 新的审核状态值
     * @return 返回布尔值，表示操作是否成功
     */
    @Override
    public Boolean updateAudit(Long id, Integer status) {
        // 更新模板的审核状态
        // 构建一个 TemplateDO 对象，设置模板 ID 和新的审核状态
        int i = templateMapper.updateById(TemplateDO.builder()
                .id(id)
                .auditStatus(status)
                .build());

        // 返回更新结果，1 表示成功，其他值表示失败
        return 1 == i;
    }

    /**
     * 获取当前用户的消息模板列表
     * <p>
     * 该方法用于查询当前登录用户所拥有的消息模板列表。
     *
     * @return 包含模板信息的 {@code List<TemplateRespVO>} 对象
     */
    @Override
    public List<TemplateRespVO> list4CurrUser() {
        // 查询当前用户创建的消息模板列表
        List<TemplateDO> templateDoList = templateMapper.selectList(new QueryWrapperX<TemplateDO>()
                .eq("creator", SecurityFrameworkUtils.getLoginUserId()) // 根据当前登录用户 ID 查询
                .orderByDesc("updateTime")); // 按更新时间降序排列

        // 将查询结果从 TemplateDO 转换为 TemplateRespVO 类型的列表
        return BeanUtil.copyToList(templateDoList, TemplateRespVO.class);
    }

}
