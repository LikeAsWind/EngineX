package org.nstep.engine.module.infra.framework.codegen.config;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.nstep.engine.module.infra.enums.codegen.CodegenFrontTypeEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;

/**
 * 代码生成器属性配置类。
 *
 * <p>该类用于存储代码生成器的配置属性，并通过@ConfigurationProperties注解与application.properties或application.yml中的配置绑定。</p>
 */
@ConfigurationProperties(prefix = "engine.codegen")
@Validated
@Data
public class CodegenProperties {

    /**
     * 生成的Java代码的基础包名。
     *
     * <p>此属性定义了生成的Java代码所属的基础包名，必须配置，不能为空。</p>
     */
    @NotNull(message = "Java 代码的基础包不能为空")
    private String basePackage;

    /**
     * 数据库模式名数组。
     *
     * <p>此属性定义了代码生成器需要处理的数据库模式名，必须配置，不能为空。</p>
     */
    @NotEmpty(message = "数据库不能为空")
    private Collection<String> dbSchemas;

    /**
     * 代码生成的前端类型。
     *
     * <p>此属性定义了代码生成的前端类型，通常对应于不同的前端框架或库。</p>
     * <p>通过枚举{@link CodegenFrontTypeEnum#getType()}来获取前端类型的值。</p>
     * <p>必须配置，不能为空。</p>
     */
    @NotNull(message = "代码生成的前端类型不能为空")
    private Integer frontType;

    /**
     * 是否生成单元测试。
     *
     * <p>此属性定义了是否为生成的代码创建单元测试。</p>
     * <p>必须配置，不能为空。</p>
     */
    @NotNull(message = "是否生成单元测试不能为空")
    private Boolean unitTestEnable;

    // 可以根据需要添加getter和setter方法，或者使用lombok提供的@Data注解自动生成
}