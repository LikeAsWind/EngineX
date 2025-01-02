package org.nstep.engine.module.message.dto.aliyun;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 阿里云短信配置类，封装了阿里云短信服务的配置信息。
 * 该类用于存储与阿里云短信服务相关的各种配置信息，包括区域ID、访问密钥、短信模板等。
 */
@Data // 使用Lombok自动生成getters, setters, toString, equals, hashCode方法
@Builder // 支持构建者模式，方便创建该类的实例
@NoArgsConstructor // 自动生成无参构造方法
@AllArgsConstructor // 自动生成包含所有成员变量的构造方法
public class AlibabaCloudSmsConfig {

    /**
     * 阿里云短信服务的区域ID，用于指定短信服务的区域。
     */
    private String regionId;

    /**
     * 阿里云的AccessKey ID，用于身份验证。
     * 在阿里云控制台获取，用于访问短信服务。
     */
    private String accessKeyId;

    /**
     * 阿里云的AccessSecret，用于身份验证。
     * 在阿里云控制台获取，配合AccessKey ID使用。
     */
    private String accessSecret;

    /**
     * 短信模板的代码，用于标识不同的短信模板。
     * 在阿里云控制台配置短信模板后会生成模板代码。
     */
    private String templateCode;

    /**
     * 短信签名，用于短信内容的签名。
     * 需要在阿里云控制台提前配置并认证通过。
     */
    private String signName;

    /**
     * 第三方服务名称，表示该配置适用的第三方服务。
     * 例如，可以用于标识某个特定的服务场景。
     */
    private String serviceName;
}
