package org.nstep.engine.framework.common.util.spring;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Spring EL 表达式的工具类
 * <p>
 * 提供了用于解析 Spring Expression Language (SpEL) 表达式的方法，支持从切面和 Bean 工厂中解析 EL 表达式。
 */
public class SpringExpressionUtils {

    /**
     * Spring EL 表达式解析器
     * <p>
     * 用于解析 Spring Expression Language (SpEL) 表达式。
     */
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    /**
     * 参数名发现器
     * <p>
     * 用于获取方法的参数名。
     */
    private static final ParameterNameDiscoverer PARAMETER_NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    // 私有构造函数，防止实例化
    private SpringExpressionUtils() {
    }

    /**
     * 从切面中，单个解析 EL 表达式的结果
     * <p>
     * 解析单个 EL 表达式，返回表达式的结果。
     *
     * @param joinPoint        切面点
     * @param expressionString EL 表达式
     * @return 执行表达式后的结果
     */
    public static Object parseExpression(JoinPoint joinPoint, String expressionString) {
        // 调用 parseExpressions 方法，获取单个表达式的结果
        Map<String, Object> result = parseExpressions(joinPoint, Collections.singletonList(expressionString));
        return result.get(expressionString);
    }

    /**
     * 从切面中，批量解析 EL 表达式的结果
     * <p>
     * 解析多个 EL 表达式，并返回每个表达式的结果。
     *
     * @param joinPoint         切面点
     * @param expressionStrings EL 表达式数组
     * @return 结果，key 为表达式，value 为对应值
     */
    public static Map<String, Object> parseExpressions(JoinPoint joinPoint, List<String> expressionStrings) {
        // 如果表达式为空，则直接返回空结果
        if (CollUtil.isEmpty(expressionStrings)) {
            return MapUtil.newHashMap();
        }

        // 第一步，构建解析的上下文 EvaluationContext
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        // 使用 Spring 的 ParameterNameDiscoverer 获取方法形参名数组
        String[] paramNames = PARAMETER_NAME_DISCOVERER.getParameterNames(method);
        // 创建 Spring 的表达式上下文对象
        EvaluationContext context = new StandardEvaluationContext();

        // 如果方法参数不为空，则将方法参数传入上下文
        if (ArrayUtil.isNotEmpty(paramNames)) {
            Object[] args = joinPoint.getArgs();
            for (int i = 0; i < paramNames.length; i++) {
                context.setVariable(paramNames[i], args[i]);
            }
        }

        // 第二步，逐个解析表达式
        Map<String, Object> result = MapUtil.newHashMap(expressionStrings.size(), true);
        expressionStrings.forEach(key -> {
            // 使用解析器解析表达式并获取值
            Object value = EXPRESSION_PARSER.parseExpression(key).getValue(context);
            result.put(key, value);
        });
        return result;
    }

    /**
     * 从 Bean 工厂，解析 EL 表达式的结果
     * <p>
     * 解析 Bean 工厂中的 EL 表达式，并返回表达式的结果。
     *
     * @param expressionString EL 表达式
     * @return 执行表达式后的结果
     */
    public static Object parseExpression(String expressionString) {
        // 如果表达式为空，则返回 null
        if (StrUtil.isBlank(expressionString)) {
            return null;
        }
        // 解析表达式
        Expression expression = EXPRESSION_PARSER.parseExpression(expressionString);
        // 创建 Spring 的表达式上下文对象
        StandardEvaluationContext context = new StandardEvaluationContext();
        // 设置 Bean 工厂解析器，支持解析 Spring 容器中的 Bean
        context.setBeanResolver(new BeanFactoryResolver(SpringUtil.getApplicationContext()));
        // 获取并返回表达式的值
        return expression.getValue(context);
    }

}
