package org.nstep.engine.framework.datapermission.core.aop;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.aopalliance.aop.Advice;
import org.nstep.engine.framework.datapermission.core.annotation.DataPermission;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.ComposablePointcut;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;

/**
 * {@link org.nstep.engine.framework.datapermission.core.annotation.DataPermission} 注解的 Advisor 实现类
 * 实现了Advisor接口，用于将DataPermission注解与对应的拦截逻辑（Interceptor）关联起来。
 * 这个类允许Spring AOP框架在执行带有@DataPermission注解的方法之前或之后执行特定的逻辑。
 */
@Getter // 使用Lombok库提供的注解，自动为类成员变量生成getter方法。
@EqualsAndHashCode(callSuper = true) // 使用Lombok库提供的注解，自动为类生成equals和hashCode方法，并且基于类的字段和父类的字段。
public class DataPermissionAnnotationAdvisor extends AbstractPointcutAdvisor {

    // 定义Advice对象，它包含了实际的拦截逻辑，这里是一个名为DataPermissionAnnotationInterceptor的实例。
    private final Advice advice;

    // 定义Pointcut对象，它定义了哪些方法会被这个Advisor匹配到。
    private final Pointcut pointcut;

    // 构造函数，初始化Advice和Pointcut对象。
    public DataPermissionAnnotationAdvisor() {
        this.advice = new DataPermissionAnnotationInterceptor(); // 初始化拦截器。
        this.pointcut = this.buildPointcut(); // 调用buildPointcut方法来构建Pointcut。
    }

    // 构建Pointcut对象，定义匹配规则。
    protected Pointcut buildPointcut() {
        // 创建一个Pointcut，用于匹配类级别的@DataPermission注解。
        Pointcut classPointcut = new AnnotationMatchingPointcut(DataPermission.class, true);
        // 创建一个Pointcut，用于匹配方法级别的@DataPermission注解。
        Pointcut methodPointcut = new AnnotationMatchingPointcut(null, DataPermission.class, true);
        // 将类和方法的Pointcut合并，使得Advisor可以匹配到带有@DataPermission注解的类和方法。
        return new ComposablePointcut(classPointcut).union(methodPointcut);
    }

}