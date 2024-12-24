package org.nstep.engine.framework.tracer.core.annotation;

import java.lang.annotation.*;

/**
 * 打印业务编号 / 业务类型注解
 * <p>
 * 使用时，需要设置 SkyWalking OAP Server 的 application.yaml 配置文件，修改 SW_SEARCHABLE_TAG_KEYS 配置项，
 * 增加 biz.type 和 biz.id 两值，然后重启 SkyWalking OAP Server 服务器。
 */
@Target({ElementType.METHOD}) // 该注解可以应用于方法
@Retention(RetentionPolicy.RUNTIME) // 该注解在运行时可用
@Inherited // 允许子类继承该注解
public @interface BizTrace {

    /**
     * 业务编号 tag 名
     */
    String ID_TAG = "biz.id"; // 业务编号的标签名

    /**
     * 业务类型 tag 名
     */
    String TYPE_TAG = "biz.type"; // 业务类型的标签名

    /**
     * @return 操作名
     */
    String operationName() default ""; // 操作名称，默认空字符串

    /**
     * @return 业务编号
     */
    String id(); // 业务编号，必填项

    /**
     * @return 业务类型
     */
    String type(); // 业务类型，必填项
}
