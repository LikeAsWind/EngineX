package org.nstep.engine.module.message.util.aliyun;

import cn.hutool.core.util.StrUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.constant.MessageDataConstants;
import org.nstep.engine.module.message.dto.aliyun.AlibabaCloudSmsConfig;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;
import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception0;


/**
 * 阿里云短信发送工具类，用于通过阿里云短信服务发送短信。
 * 该工具类封装了阿里云短信发送的过程，包括创建请求、发送请求、处理响应等操作。
 */
@Slf4j // 使用Lombok生成日志记录器
public class AlibabaCloudSMSSendUtils {

    /**
     * 发送短信的方法，通过阿里云短信服务发送短信。
     *
     * @param config       账号配置信息，包含区域ID、访问密钥等信息
     * @param phoneNumbers 目标手机号，支持多个手机号（逗号分隔）
     * @param param        短信模板的参数（如验证码等），用于填充短信模板中的占位符
     * @return 返回阿里云短信发送的回执ID（BizId），成功时返回该ID，失败时抛出异常
     */
    public static String sendMessage(AlibabaCloudSmsConfig config, String phoneNumbers, String param) {
        // 创建阿里云短信服务的Profile对象，包含区域ID、AccessKey ID 和 AccessKey Secret
        DefaultProfile profile = DefaultProfile.getProfile(config.getRegionId(), config.getAccessKeyId(), config.getAccessSecret());
        IAcsClient client = new DefaultAcsClient(profile); // 创建短信客户端

        // 创建发送短信请求
        SendSmsRequest request = new SendSmsRequest();
        request.setPhoneNumbers(phoneNumbers); // 设置目标手机号
        request.setSignName(config.getSignName()); // 设置短信签名
        request.setTemplateCode(config.getTemplateCode()); // 设置短信模板代码
        if (!StrUtil.EMPTY.equals(param)) {
            // 如果参数不为空，设置短信模板参数（例如验证码）
            request.setTemplateParam(param);
        }

        try {
            // 发送短信请求并获取响应
            SendSmsResponse response = client.getAcsResponse(request);
            String code = response.getCode(); // 获取响应码
            if (MessageDataConstants.OK.equals(code)) {
                log.info("短信发送成功:{}", response); // 记录成功日志
                // 返回短信发送的回执ID（BizId）
                return response.getBizId();
            } else {
                // 如果响应码不是成功，记录失败日志并抛出异常
                log.error("短信发送失败:响应码code报错信息:{}", response.getMessage());
                throw exception(
                        ErrorCodeConstants.SMS_SEND_FAILURE,
                        response.getMessage()
                );
            }
        } catch (ClientException e) {
            // 如果发生异常，捕获并抛出自定义异常
            throw exception(
                    ErrorCodeConstants.EMPTY_OBJECT,
                    e.getMessage()
            );
        }
    }
}
