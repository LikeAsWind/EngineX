package org.nstep.engine.module.message.handler;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.nstep.engine.module.message.domain.SendTaskInfo;
import org.nstep.engine.module.message.dto.content.SmsContentModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 渠道handler抽象类
 * <p>
 * 该类作为所有具体渠道处理器的基类，提供了一个通用的发送任务处理方法 `handler`，以及一个抽象方法 `doHandler`，用于不同渠道的具体实现。
 * 该类定义了如何处理发送任务的基础框架，子类需要实现 `doHandler` 方法来执行特定的任务逻辑。
 */
public abstract class ChannelHandler {

    /**
     * 处理发送任务
     * <p>
     * 该方法是公开的入口方法，所有具体的渠道处理器都可以调用该方法来处理任务。内部调用了 `doHandler` 方法，实际的任务处理逻辑由子类实现。
     *
     * @param sendTaskInfo 发送任务的详细信息，包括发送的内容和目标等。
     */
    public void handler(SendTaskInfo sendTaskInfo) {
        // 调用具体实现的 doHandler 方法进行任务处理
        doHandler(sendTaskInfo);
    }

    // 抽象方法，子类必须实现，具体的任务处理逻辑将在这里定义
    abstract void doHandler(SendTaskInfo sendTaskInfo);

    /**
     * 长链接转短链，功能尚未实现
     * <p>
     * 该方法用于在短信内容中识别并替换长链接为短链接，但当前功能未实现。
     * 主要是解析短信内容中的 URL 占位符，并进行相应的长链接转换操作。
     *
     * @param smsContentModel 短信内容模型，包含内容和可能的长链接
     * @return 返回修改后的短信内容（目前仅解析内容，长链转短链功能未实现）
     */
    public JSONObject getSmsContent(SmsContentModel smsContentModel) {
        // 解析短信内容
        JSONObject jsonObject = JSON.parseObject(smsContentModel.getContent());

        // 如果 URL 不为空，则尝试查找占位符并处理
        if (StrUtil.isNotBlank(smsContentModel.getUrl())) {
            // 获取所有 URL 占位符
            List<String> urlVariables = getUrlVariables(smsContentModel.getUrl());

            // 如果 URL 占位符不为空，则进行处理
            if (CollectionUtil.isNotEmpty(urlVariables)) {
                // 遍历每个占位符并进行处理
                for (String urlVariable : urlVariables) {
                    // 获取原始长链接
                    String url = (String) jsonObject.get(urlVariable);
                    // 长链转短链的功能尚未实现
                }
            }
            return jsonObject;
        } else {
            return jsonObject;
        }
    }

    /**
     * 获取URL中的占位符变量
     * <p>
     * 该方法用于从给定的 URL 中提取占位符变量（即形如 ${key} 的部分），返回所有的占位符键。
     *
     * @param url 包含占位符的URL字符串
     * @return 返回一个包含所有占位符键的列表
     */
    public List<String> getUrlVariables(String url) {
        List<String> urls = new ArrayList<>();

        // 使用正则表达式匹配占位符
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)}");
        Matcher matcher = pattern.matcher(url);

        // 查找所有匹配的占位符
        while (matcher.find()) {
            String key = matcher.group(1); // 获取占位符键，不包括 "${" 和 "}"
            urls.add(key); // 将占位符键添加到列表中
        }

        return urls;
    }
}
