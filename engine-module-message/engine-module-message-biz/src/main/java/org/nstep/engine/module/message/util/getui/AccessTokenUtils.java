package org.nstep.engine.module.message.util.getui;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.ContentType;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.module.message.constant.SendChanelUrlConstant;
import org.nstep.engine.module.message.dto.push.getui.GeTuiConfig;
import org.nstep.engine.module.message.dto.push.getui.GeTuiTokenResultDTO;
import org.nstep.engine.module.message.dto.push.getui.QueryTokenParamDTO;


/**
 * 个推Token工具类
 * 该类用于获取个推（GeTui）服务的用户Token，Token用于后续的消息推送操作。
 * 通过个推的API接口获取Token，并进行签名校验。
 */
@Slf4j
public class AccessTokenUtils {

    /**
     * 获取个推用户Token
     * 该方法通过调用个推API接口，使用appKey、masterSecret和时间戳生成签名，获取个推的Token。
     * <a href="https://docs.getui.com/getui/server/rest_v2/token/">个推Token API文档</a>
     *
     * @param account GeTuiConfig 对象，包含个推服务的配置信息，如appKey和masterSecret
     * @return GeTuiTokenResultDTO.DataDTO 返回包含Token信息的DataDTO对象
     */
    public static GeTuiTokenResultDTO.DataDTO getGeTuiToken(GeTuiConfig account) {
        try {
            // 构建个推Token请求的URL
            String url = SendChanelUrlConstant.GE_TUI_BASE_URL + account.getAppId() + SendChanelUrlConstant.GE_TUI_AUTH;
            // 获取当前时间戳
            long timestamp = System.currentTimeMillis();
            // 生成签名，签名由appKey、时间戳和masterSecret组成，使用SHA-256加密
            String sign = SecureUtil.sha256(account.getAppKey() + timestamp + account.getMasterSecret());

            // 创建请求参数对象
            QueryTokenParamDTO paramDTO = QueryTokenParamDTO.builder()
                    .sign(sign)  // 签名
                    .appKey(account.getAppKey())  // appKey
                    .timestamp(String.valueOf(timestamp))  // 时间戳
                    .build();

            // 发送POST请求获取Token
            String body = HttpRequest.post(url)
                    .header(Header.CONTENT_TYPE.getValue(), ContentType.JSON.getValue())
                    .body(JSON.toJSONString(paramDTO))  // 将请求参数转为JSON字符串
                    .timeout(20000)  // 设置超时时间
                    .execute().body();  // 执行请求并获取响应体

            // 解析响应体为GeTuiTokenResultDTO对象
            GeTuiTokenResultDTO result = JSON.parseObject(body, GeTuiTokenResultDTO.class);

            // 如果返回结果的code为0，表示请求成功，返回Token数据
            if (result.getCode().equals(0)) {
                return result.getData();
            }
        } catch (Exception e) {
            // 如果发生异常，记录错误日志并返回null
            log.error("个推token 获取出现异常:{}", Throwables.getStackTraceAsString(e));
        }
        // 如果获取Token失败，返回null
        return null;
    }
}
