package org.nstep.engine.framework.ratelimiter.core.keyresolver.impl;

import cn.hutool.core.util.ArrayUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.nstep.engine.framework.ratelimiter.core.annotation.RateLimiter;
import org.nstep.engine.framework.ratelimiter.core.keyresolver.RateLimiterKeyResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 基于 Spring EL 表达式的 {@link RateLimiterKeyResolver} 实现类
 * <p>
 * 该类使用 Spring Expression Language（SpEL）来动态解析限流 Key。可以根据方法参数、方法名等信息，
 * 使用表达式计算出唯一的 Key，适用于需要灵活、动态的限流场景。
 */
public class ExpressionRateLimiterKeyResolver implements RateLimiterKeyResolver {

    // 用于获取方法参数名的工具类
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    // Spring 表达式解析器
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * 获取目标方法
     * <p>
     * 该方法用于处理切面中的方法，判断它是声明在类中还是接口中，并返回正确的方法对象。
     *
     * @param point AOP 切面对象，提供方法签名和参数信息
     * @return 目标方法对象
     */
    private static Method getMethod(JoinPoint point) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 如果方法声明在类中，直接返回
        if (!method.getDeclaringClass().isInterface()) {
            return method;
        }

        // 如果方法声明在接口中，获取目标类的方法
        try {
            return point.getTarget().getClass().getDeclaredMethod(
                    point.getSignature().getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析限流的 Key
     * <p>
     * 该方法使用 Spring EL 表达式动态解析限流的 Key。根据方法参数、方法名等信息，
     * 结合表达式计算出唯一的限流 Key。可以通过注解中的 `keyArg` 属性来指定表达式。
     *
     * @param joinPoint   AOP 切面，提供方法签名和参数
     * @param rateLimiter 限流注解对象，包含限流配置
     * @return 返回解析后的限流 Key
     */
    @Override
    public String resolver(JoinPoint joinPoint, RateLimiter rateLimiter) {
        // 获取被拦截方法的参数名列表
        Method method = getMethod(joinPoint);
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = this.parameterNameDiscoverer.getParameterNames(method);

        // 准备 Spring EL 表达式解析的上下文
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        if (ArrayUtil.isNotEmpty(parameterNames)) {
            for (int i = 0; i < parameterNames.length; i++) {
                // 将方法参数名和参数值添加到上下文中
                evaluationContext.setVariable(parameterNames[i], args[i]);
            }
        }

        // 使用 Spring EL 解析表达式
        Expression expression = expressionParser.parseExpression(rateLimiter.keyArg());
        return expression.getValue(evaluationContext, String.class);
    }

}
