package org.nstep.engine.module.message.util;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import org.nstep.engine.framework.common.exception.ServiceException;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.constant.WeChatConstants;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dto.WeChatServiceAccountContentModel;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.nstep.engine.module.message.service.content.WeChatServiceAccountService;
import org.springframework.stereotype.Component;
import org.springframework.util.PropertyPlaceholderHelper;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * 处理模板消息内容占位符的工具类
 * <p>
 * 主要功能：
 * 1. 提取模板消息中的占位符变量名。
 * 2. 根据输入的参数替换消息内容中的占位符。
 * </p>
 */
@Slf4j
@Component
public class ContentHolderUtil {

    /**
     * 注入微信服务号服务，用于与微信API交互。
     */
    @Resource
    private WeChatServiceAccountService weChatServiceAccountService;

    /**
     * 占位符帮助类，用于处理字符串中的占位符替换。
     * 例如："Hello, ${name}" 会被替换为 "Hello, John"。
     */
    private static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER = new PropertyPlaceholderHelper(MessageDataConstants.PLACE_HOLDER_PREFIX, MessageDataConstants.PLACE_HOLDER_SUFFIX);

    /**
     * 提取模板消息中的占位符变量名。
     * <p>
     * 占位符格式：${变量名}。
     * 支持普通模板消息和微信服务号消息的特殊处理。
     * </p>
     *
     * @param messageTemplate 模板消息对象，包含消息内容和发送渠道。
     * @return 包含所有占位符变量名的列表。
     * @throws ServiceException 如果微信服务号消息中模板ID不存在或占位符数量超出限制。
     */
    public List<String> getVariables(TemplateDO messageTemplate) {
        // 获取消息内容
        String context = messageTemplate.getMsgContent();
        // 使用 Set 存储占位符变量名，避免重复
        Set<String> sKeys = new HashSet<>();
        // 定义正则表达式匹配占位符格式 "${变量名}"。
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(context);

        // 遍历消息内容中的占位符并提取变量名
        while (matcher.find()) {
            String key = matcher.group(1); // 提取占位符中的变量名
            sKeys.add(key); // 添加到集合中
        }

        // 如果消息的发送渠道是微信服务号，进行特殊处理
        if (MessageDataConstants.WECHAT_SERVICE_ACCOUNT.equals(messageTemplate.getSendChannel())) {
            // 微信服务号的占位符数量限制为1
            if (sKeys.size() > 1) {
                throw exception(ErrorCodeConstants.EXCEEDED_DOMAIN_REDIRECT_LIMIT);
            }

            // 如果存在占位符，清空原有变量名集合并添加微信服务号的URL占位符名称
            if (!sKeys.isEmpty()) {
                sKeys.clear();
                sKeys.add(WeChatConstants.WECHAT_SERVICE_ACCOUNT_URL_NAME);
            }

            // 调用微信API查询模板详情并提取模板内容中的占位符
            WeChatServiceAccountContentModel contentModel = JSON.parseObject(messageTemplate.getMsgContent(), WeChatServiceAccountContentModel.class);
            WxMpTemplate wxMpTemplate = weChatServiceAccountService.queryWxTemplateDetailByTemplateId(messageTemplate.getSendAccount(), contentModel.getTemplateId());

            // 如果模板ID无效或不存在，抛出异常
            if (Objects.isNull(wxMpTemplate)) {
                throw new ServiceException(ErrorCodeConstants.TEMPLATE_ID_NOT_EXIST);
            }

            // 定义正则表达式匹配微信模板格式 "{{变量名.xxx}}"
            Pattern wxPattern = Pattern.compile("\\{\\{([^.]+)\\..*?}}");
            Matcher wxMatcher = wxPattern.matcher(wxMpTemplate.getContent());

            // 遍历微信模板内容中的占位符并提取变量名
            while (wxMatcher.find()) {
                String key = wxMatcher.group(1); // 提取变量名
                sKeys.add(key); // 添加到集合中
            }
        }

        // 将变量名集合转换为列表并返回
        return new ArrayList<>(sKeys);
    }

    /**
     * 替换消息内容中的占位符。
     * <p>
     * 占位符的替换数据以 JSON 格式提供，方法会将其解析为键值对。
     * </p>
     *
     * @param content 消息内容，包含占位符。
     * @param param   占位符对应的替换数据（JSON格式）。
     * @return 替换后的消息内容。
     * @throws ServiceException 如果占位符解析失败或参数无效。
     */
    public String replacePlaceHolder(String content, String param) {
        Properties properties;
        try {
            // 将 JSON 字符串转换为 Properties 对象
            properties = convertJsonToProperties(param);
        } catch (Exception e) {
            log.error("占位符数据解析失败:{}", Throwables.getStackTraceAsString(e));
            throw exception(ErrorCodeConstants.PLACEHOLDER_RESOLUTION_FAILURE);
        }
        // 确保 properties 不为空
        assert properties != null;
        // 使用占位符帮助类替换消息内容中的占位符
        return PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders(content, properties);
    }

    /**
     * 判断字符串是否为合法的 JSON 格式。
     *
     * @param json JSON字符串。
     * @return 如果是合法的 JSON 格式，返回 true；否则返回 false。
     */
    public static boolean isJson(String json) {
        try {
            // 使用 ObjectMapper 尝试解析 JSON 字符串
            new ObjectMapper().readTree(json);
            return true;
        } catch (IOException e) {
            return false; // 如果解析失败，返回 false
        }
    }

    /**
     * 将 JSON 字符串转换为 Properties 对象。
     * <p>
     * 该方法会将 JSON 数据中的键值对转换为 Properties 对象，
     * 用于占位符替换时提供参数支持。
     * </p>
     *
     * @param json JSON字符串。
     * @return Properties 对象，包含 JSON 中的键值对。
     * @throws JsonProcessingException 如果 JSON 格式无效，抛出异常。
     */
    public static Properties convertJsonToProperties(String json) throws JsonProcessingException {
        if (isJson(json)) {
            // 使用 ObjectMapper 将 JSON 转换为 Map
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {
            });
            Properties properties = new Properties();

            // 遍历 Map 并将数据存入 Properties 对象
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                properties.put(entry.getKey(), entry.getValue().toString());
            }
            return properties;
        }
        // 如果输入不是 JSON 格式，返回 null 或根据实际需求抛出异常
        return null;
    }
}
