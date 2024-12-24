package org.springframework.messaging.handler.invocation;

import lombok.Setter;
import org.nstep.engine.framework.tenant.core.context.TenantContextHolder;
import org.nstep.engine.framework.tenant.core.util.TenantUtils;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.HandlerMethod;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

import static org.nstep.engine.framework.web.core.util.WebFrameworkUtils.HEADER_TENANT_ID;


/**
 * {@link HandlerMethod} 的扩展类，用于在当前 HTTP 请求上下文中通过一组 {@link HandlerMethodArgumentResolver} 解析方法参数值并调用底层方法。
 * <p>
 * 针对 rabbitmq-spring 和 kafka-spring，目前没有合适的扩展点，可以在消费者（Consumer）消费消息之前，读取消息 Header 中的 tenant-id 并设置到 {@link TenantContextHolder} 中。
 *
 * @since 4.0
 */
public class InvocableHandlerMethod extends HandlerMethod {

    private static final Object[] EMPTY_ARGS = new Object[0];  // 空参数数组

    private HandlerMethodArgumentResolverComposite resolvers = new HandlerMethodArgumentResolverComposite();  // 参数解析器
    @Setter
    private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();  // 参数名称发现器

    /**
     * 从 {@code HandlerMethod} 创建一个实例。
     *
     * @param handlerMethod 原始的 HandlerMethod 实例
     */
    public InvocableHandlerMethod(HandlerMethod handlerMethod) {
        super(handlerMethod);
    }

    /**
     * 从一个 bean 实例和方法创建一个实例。
     *
     * @param bean   bean 实例
     * @param method 方法对象
     */
    public InvocableHandlerMethod(Object bean, Method method) {
        super(bean, method);
    }

    /**
     * 使用给定的 bean 实例、方法名和参数类型构造一个新的 handler 方法。
     *
     * @param bean           bean 对象
     * @param methodName     方法名
     * @param parameterTypes 方法参数类型
     * @throws NoSuchMethodException 如果方法找不到
     */
    public InvocableHandlerMethod(Object bean, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        super(bean, methodName, parameterTypes);
    }

    /**
     * 设置 {@link HandlerMethodArgumentResolver HandlerMethodArgumentResolvers}，用于解析方法参数值。
     *
     * @param argumentResolvers 参数解析器集合
     */
    public void setMessageMethodArgumentResolvers(HandlerMethodArgumentResolverComposite argumentResolvers) {
        this.resolvers = argumentResolvers;
    }


    /**
     * 在给定消息的上下文中解析方法参数值后调用方法。
     * <p>参数值通常通过 {@link HandlerMethodArgumentResolver HandlerMethodArgumentResolvers} 解析。
     * 然而，{@code providedArgs} 参数可以直接提供匹配类型的参数值，而无需解析。
     * <p>委托给 {@link #getMethodArgumentValues} 并使用解析后的参数调用 {@link #doInvoke}。
     *
     * @param message      当前处理的消息
     * @param providedArgs 直接提供的参数值
     * @return 调用方法时返回的原始值
     * @throws Exception 如果找不到合适的参数解析器，或者方法抛出异常
     * @see #getMethodArgumentValues
     * @see #doInvoke
     */
    @Nullable
    public Object invoke(Message<?> message, Object... providedArgs) throws Exception {
        // 获取方法参数的值
        Object[] args = getMethodArgumentValues(message, providedArgs);
        if (logger.isTraceEnabled()) {
            logger.trace("Arguments: " + Arrays.toString(args));  // 日志记录参数
        }

        // 处理租户 ID
        Long tenantId = parseTenantId(message);  // 从消息中解析租户 ID
        // 如果有租户 ID，使用租户上下文执行方法
        return TenantUtils.execute(tenantId, () -> doInvoke(args));  // 在租户上下文中执行方法
    }

    /**
     * 从消息中解析租户 ID。
     *
     * @param message 消息对象
     * @return 租户 ID 或 null
     */
    private Long parseTenantId(Message<?> message) {
        Object tenantId = message.getHeaders().get(HEADER_TENANT_ID);  // 获取消息头中的租户 ID
        if (tenantId == null) {
            return null;  // 如果没有租户 ID，返回 null
        }
        // 根据不同的类型进行转换
        if (tenantId instanceof Long) {
            return (Long) tenantId;
        }
        if (tenantId instanceof Number) {
            return ((Number) tenantId).longValue();
        }
        if (tenantId instanceof String) {
            return Long.parseLong((String) tenantId);
        }
        if (tenantId instanceof byte[]) {
            return Long.parseLong(new String((byte[]) tenantId));
        }
        // 如果租户 ID 类型不合法，抛出异常
        throw new IllegalArgumentException("未知的数据类型：" + tenantId);
    }

    /**
     * 获取当前消息的参数值，检查提供的参数值并回退到配置的参数解析器。
     * <p>最终生成的数组将传递给 {@link #doInvoke} 方法。
     *
     * @since 5.1.2
     */
    protected Object[] getMethodArgumentValues(Message<?> message, Object... providedArgs) throws Exception {
        MethodParameter[] parameters = getMethodParameters();  // 获取方法的参数信息
        if (ObjectUtils.isEmpty(parameters)) {
            return EMPTY_ARGS;  // 如果没有参数，返回空数组
        }

        Object[] args = new Object[parameters.length];  // 参数数组
        for (int i = 0; i < parameters.length; i++) {
            MethodParameter parameter = parameters[i];
            parameter.initParameterNameDiscovery(this.parameterNameDiscoverer);  // 初始化参数名称发现器
            args[i] = findProvidedArgument(parameter, providedArgs);  // 查找提供的参数值
            if (args[i] != null) {
                continue;  // 如果参数值已提供，则跳过
            }
            // 如果没有提供参数值，检查是否支持解析该参数
            if (!this.resolvers.supportsParameter(parameter)) {
                throw new MethodArgumentResolutionException(message, parameter, formatArgumentError(parameter, "No suitable resolver"));
            }
            try {
                // 使用参数解析器解析参数
                args[i] = this.resolvers.resolveArgument(parameter, message);
            } catch (Exception ex) {
                // 异常处理，日志记录解析错误
                if (logger.isDebugEnabled()) {
                    String exMsg = ex.getMessage();
                    if (exMsg != null && !exMsg.contains(parameter.getExecutable().toGenericString())) {
                        logger.debug(formatArgumentError(parameter, exMsg));
                    }
                }
                throw ex;  // 抛出异常
            }
        }
        return args;  // 返回解析后的参数数组
    }

    /**
     * 使用给定的参数值调用 handler 方法。
     *
     * @param args 方法参数
     * @return 方法返回值
     * @throws Exception 调用方法时可能抛出的异常
     */
    @Nullable
    protected Object doInvoke(Object... args) throws Exception {
        try {
            return getBridgedMethod().invoke(getBean(), args);  // 调用方法
        } catch (IllegalArgumentException ex) {
            // 如果方法参数不合法，抛出异常
            assertTargetBean(getBridgedMethod(), getBean(), args);
            String text = (ex.getMessage() == null || ex.getCause() instanceof NullPointerException) ? "Illegal argument" : ex.getMessage();
            throw new IllegalStateException(formatInvokeError(text, args), ex);
        } catch (InvocationTargetException ex) {
            // 解包 InvocationTargetException 中的目标异常
            Throwable targetException = ex.getTargetException();
            if (targetException instanceof RuntimeException runtimeException) {
                throw runtimeException;  // 重新抛出 RuntimeException
            } else if (targetException instanceof Error error) {
                throw error;  // 重新抛出 Error
            } else if (targetException instanceof Exception exception) {
                throw exception;  // 重新抛出 Exception
            } else {
                throw new IllegalStateException(formatInvokeError("Invocation failure", args), targetException);
            }
        }
    }

    /**
     * 获取异步返回值的类型。
     *
     * @param returnValue 方法的返回值
     * @return 异步结果方法参数
     */
    MethodParameter getAsyncReturnValueType(@Nullable Object returnValue) {
        return new AsyncResultMethodParameter(returnValue);  // 返回异步结果参数
    }

    /**
     * 异步结果方法参数类，用于处理返回值类型。
     */
    private class AsyncResultMethodParameter extends AnnotatedMethodParameter {

        @Nullable
        private final Object returnValue;  // 返回值
        private final ResolvableType returnType;  // 返回值类型

        public AsyncResultMethodParameter(@Nullable Object returnValue) {
            super(-1);
            this.returnValue = returnValue;
            this.returnType = ResolvableType.forType(super.getGenericParameterType()).getGeneric();  // 获取返回值的类型
        }

        protected AsyncResultMethodParameter(AsyncResultMethodParameter original) {
            super(original);
            this.returnValue = original.returnValue;
            this.returnType = original.returnType;
        }

        @Override
        public Class<?> getParameterType() {
            if (this.returnValue != null) {
                return this.returnValue.getClass();  // 返回值类型
            }
            if (!ResolvableType.NONE.equals(this.returnType)) {
                return this.returnType.toClass();  // 返回解析的类型
            }
            return super.getParameterType();  // 默认类型
        }

        @Override
        public Type getGenericParameterType() {
            return this.returnType.getType();  // 返回泛型类型
        }

        @Override
        public AsyncResultMethodParameter clone() {
            return new AsyncResultMethodParameter(this);  // 克隆方法参数
        }
    }
}
