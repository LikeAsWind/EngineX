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
import org.nstep.engine.module.message.controller.admin.content.dto.WeChatServiceAccountContentModel;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
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
 * 处理模板消息内容占位符工具
 */
@Slf4j
@Component
public class ContentHolderUtil {

    @Resource
    private WeChatServiceAccountService weChatServiceAccountService;

    private static final PropertyPlaceholderHelper PROPERTY_PLACEHOLDER_HELPER =
            new PropertyPlaceholderHelper(MessageDataConstants.PLACE_HOLDER_PREFIX, MessageDataConstants.PLACE_HOLDER_SUFFIX);

    /**
     * 获取占位符变量名
     */
    public List<String> getVariables(TemplateDO messageTemplate) {

        String context = messageTemplate.getMsgContent();
        Set<String> skeys = new HashSet<>();
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(context);

        while (matcher.find()) {
            String key = matcher.group(1); // 获取占位符键，不包括 "${" 和 "}"
            //将占位符变量添加进set
            skeys.add(key);
        }
        if (MessageDataConstants.WECHAT_SERVICE_ACCOUNT.equals(messageTemplate.getSendChannel())) {
            if (skeys.size() > 1) {
                throw exception(ErrorCodeConstants.EXCEEDED_DOMAIN_REDIRECT_LIMIT);
            }
            //链接存在占位符 替换用户输入的占位符名称
            if (!skeys.isEmpty()) {
                skeys.clear();
                skeys.add(WeChatConstants.WECHAT_SERVICE_ACCOUNT_URL_NAME);
            }
            //微信服务号需要调用微信API查询占位符
            WeChatServiceAccountContentModel contentModel =
                    JSON.parseObject(messageTemplate.getMsgContent(), WeChatServiceAccountContentModel.class);
            //WxMpTemplate wxMpTemplate = weChatServiceAccountService.queryWxTemplateDetailByTemplateId(messageTemplate.getSendAccount(), contentModel.getTemplateId());
            WxMpTemplate wxMpTemplate = null;
            if (Objects.isNull(wxMpTemplate)) {
                throw new ServiceException(ErrorCodeConstants.TEMPLATE_ID_NOT_EXIST);
            }
            Pattern wxPattern = Pattern.compile("\\{\\{([^.]+)\\..*?}}");
            Matcher wxMatcher = wxPattern.matcher(wxMpTemplate.getContent());
            while (wxMatcher.find()) {
                String key = wxMatcher.group(1);
                skeys.add(key);
            }
        }
        //转list
        return new ArrayList<>(skeys);
    }

    /**
     * 替换占位符
     */
    public String replacePlaceHolder(String content, String param) {

        Properties properties;
        try {
            properties = convertJsonToProperties(param);
        } catch (Exception e) {
            log.error("占位符数据解析失败:{}", Throwables.getStackTraceAsString(e));
            throw exception(ErrorCodeConstants.PLACEHOLDER_RESOLUTION_FAILURE);
        }
        assert properties != null;
        return PROPERTY_PLACEHOLDER_HELPER.replacePlaceholders(content, properties);
    }


    public static boolean isJson(String json) {
        try {
            new ObjectMapper().readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public static Properties convertJsonToProperties(String json) throws JsonProcessingException {
        if (isJson(json)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(json, new TypeReference<>() {});
            Properties properties = new Properties();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                properties.put(entry.getKey(), entry.getValue().toString());
            }
            return properties;

        }
        // 或者抛出异常，根据实际情况处理
        return null;
    }
}
