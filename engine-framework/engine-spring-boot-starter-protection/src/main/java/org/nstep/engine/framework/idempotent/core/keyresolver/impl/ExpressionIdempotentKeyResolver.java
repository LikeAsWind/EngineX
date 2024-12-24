package org.nstep.engine.framework.idempotent.core.keyresolver.impl;

import cn.hutool.core.util.ArrayUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.nstep.engine.framework.idempotent.core.annotation.Idempotent;
import org.nstep.engine.framework.idempotent.core.keyresolver.IdempotentKeyResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 基于 Spring EL 表达式的幂等 Key 解析器
 * <p>
 * 该解析器使用 Spring 表达式语言（Spring EL）来动态解析幂等性 Key。通过传入的表达式，可以灵活地选择方法参数中的任意一个或多个作为幂等性 Key。
 * </p>
 */
public class ExpressionIdempotentKeyResolver implements IdempotentKeyResolver {

    // 用于获取方法参数名的工具
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    // Spring EL 表达式解析器
    private final ExpressionParser expressionParser = new SpelExpressionParser();

    /**
     * 获取被拦截方法的 Method 对象
     * <p>
     * 该方法会处理类和接口中声明的方法，确保能正确获取方法的声明。
     * </p>
     *
     * @param point AOP 切面对象
     * @return 方法对象
     */
    private static Method getMethod(JoinPoint point) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        // 如果方法声明在类上，直接返回
        if (!method.getDeclaringClass().isInterface()) {
            return method;
        }

        // 如果方法声明在接口上，通过反射获取方法
        try {
            return point.getTarget().getClass().getDeclaredMethod(
                    point.getSignature().getName(), method.getParameterTypes());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 解析幂等性 Key
     * <p>
     * 该方法通过 Spring EL 表达式解析传入的 `keyArg` 参数。首先获取方法参数名列表，然后将方法参数和参数名存入上下文中，最后使用 Spring EL 解析表达式。
     * </p>
     *
     * @param joinPoint  AOP 切面对象，包含了方法签名和参数信息
     * @param idempotent {@link Idempotent} 注解，包含了幂等性相关的配置
     * @return 生成的幂等性 Key
     */
    @Override
    public String resolver(JoinPoint joinPoint, Idempotent idempotent) {
        // 获取被拦截方法的 Method 对象
        Method method = getMethod(joinPoint);
        // 获取方法参数
        Object[] args = joinPoint.getArgs();
        // 获取方法参数名
        String[] parameterNames = this.parameterNameDiscoverer.getParameterNames(method);

        // 创建 Spring EL 表达式的上下文
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        if (ArrayUtil.isNotEmpty(parameterNames)) {
            // 将方法参数名和对应的值放入上下文
            for (int i = 0; i < parameterNames.length; i++) {
                evaluationContext.setVariable(parameterNames[i], args[i]);
            }
        }

        // 使用 Spring EL 解析表达式
        Expression expression = expressionParser.parseExpression(idempotent.keyArg());
        // 返回解析后的值，作为幂等性 Key
        return expression.getValue(evaluationContext, String.class);
    }

}
