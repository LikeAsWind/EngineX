package org.nstep.engine.module.message.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.csv.CsvData;
import cn.hutool.core.text.csv.CsvReadConfig;
import cn.hutool.core.text.csv.CsvRow;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dto.message.TemplateSend;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * CSV工具类，提供读取CSV文件并生成发送请求表单的功能。
 */
@Component
@Slf4j
public class CsvFileUtil {

    @Resource
    private ContentHolderUtil contentHolderUtil; // 自动注入ContentHolderUtil，用于获取模板变量

    /**
     * 读取CSV文件并构建发送请求表单。
     *
     * @param messageTemplate 模板对象，包含任务相关的信息
     * @param sender          发送方的ID
     * @return 返回生成的发送请求表单（TemplateSendReqVO）
     */
    public TemplateSend readCsvBuildSendFom(TemplateDO messageTemplate, Long sender) {

        // 获取模板中的变量名集合，用于后续生成请求表单中的变量
        List<String> varNames = contentHolderUtil.getVariables(messageTemplate);
        TemplateSend sendForm = new TemplateSend(); // 初始化发送请求表单对象

        try {
            // 使用CSV读取工具获取CSV文件数据
            cn.hutool.core.text.csv.CsvReader reader = CsvUtil.getReader(new CsvReadConfig().setContainsHeader(true));
            // 读取CSV文件内容（使用UTF-8字符集），返回CsvData对象
            CsvData data = reader.read(ResourceUtil.getReader(messageTemplate.getCronCrowdPath(), CharsetUtil.CHARSET_UTF_8));
            StringBuilder receivers = new StringBuilder(); // 用于存储接收者信息
            List<JSONObject> variables = new ArrayList<>(); // 用于存储每一行的变量数据
            List<CsvRow> rows = data.getRows(); // 获取CSV文件的所有行数据

            // 如果CSV文件为空，返回错误信息
            if (CollectionUtil.isEmpty(rows)) {
                log.error("消息:{} 人群文件为空", messageTemplate.getName());
                sendForm.setIsNeedBreak(true); // 标记需要终止后续操作
                sendForm.setResponse(
                        CommonResult.error0(
                                ErrorCodeConstants.MESSAGE_CROWD_FILE_EMPTY.getCode(),
                                ErrorCodeConstants.MESSAGE_CROWD_FILE_EMPTY.getMsg(),
                                messageTemplate.getName()
                        )
                );
                return sendForm; // 返回错误表单
            }

            // 遍历每一行CSV数据
            for (CsvRow row : rows) {
                // 获取当前行的表头列名与列值的映射
                Map<String, String> fieldMap = row.getFieldMap();
                // 拼接接收者字段，多个接收者用分隔符隔开
                receivers.append(fieldMap.get(MessageDataConstants.CRON_FILE_RECEIVER)).append(MessageDataConstants.SEPARATOR);
                JSONObject jsonObject = new JSONObject(); // 存储当前行的变量数据
                // 遍历变量名集合，填充每行数据中的相应字段
                for (String varName : varNames) {
                    jsonObject.set(varName, fieldMap.get(varName)); // 将变量名和对应值放入JSON对象
                }
                variables.add(jsonObject); // 将当前行的变量数据添加到变量集合
            }

            // 移除最后一个分隔符
            receivers.deleteCharAt(receivers.lastIndexOf(MessageDataConstants.SEPARATOR));
            sendForm.setReceivers(String.valueOf(receivers)); // 设置接收者信息

            // 如果变量名集合非空，设置变量数据到发送表单
            if (CollectionUtil.isNotEmpty(varNames)) {
                sendForm.setVariables(JSONUtil.toJsonStr(variables)); // 将变量数据转换为JSON字符串
                sendForm.setIsExitVariables(varNames.size()); // 设置占位符数量
            } else {
                sendForm.setVariables(StrUtil.EMPTY); // 如果没有变量，设置为空
                sendForm.setIsExitVariables(0); // 没有占位符
            }

            // 设置发送请求表单的其他信息
            sendForm.setMessageTemplateId(messageTemplate.getId()); // 设置消息模板ID
            sendForm.setSendChannel(messageTemplate.getSendChannel()); // 设置发送渠道
            sendForm.setSender(sender); // 设置发送方ID
        } catch (Exception e) {
            // 如果发生异常，标记需要终止操作，并返回错误信息
            sendForm.setIsNeedBreak(true);
            sendForm.setResponse(
                    CommonResult.error0(
                            ErrorCodeConstants.READ_CSV_FAILED.getCode(),
                            ErrorCodeConstants.READ_CSV_FAILED.getMsg(),
                            e.getMessage()
                    )
            );
            return sendForm;
        }
        return sendForm;
    }
}
