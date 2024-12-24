package org.nstep.engine.framework.datapermission.core.aop;

import lombok.Getter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.datapermission.core.annotation.DataPermission;
import org.springframework.core.MethodClassKey;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现了MethodInterceptor接口，用于在方法执行前后处理数据权限逻辑。
 * {@link DataPermission} 注解的拦截器
 * 1. 在执行方法前，将 @DataPermission 注解入栈
 * 2. 在执行方法后，将 @DataPermission 注解出栈
 */
@Getter
@DataPermission // 该注解，用于 {@link DATA_PERMISSION_NULL} 的空对象
public class DataPermissionAnnotationInterceptor implements MethodInterceptor {

    /**
     * DataPermission空对象，用于方法无DataPermission注解时，使用DATA_PERMISSION_NULL进行占位。
     */
    static final DataPermission DATA_PERMISSION_NULL = DataPermissionAnnotationInterceptor.class.getAnnotation(DataPermission.class);

    /**
     * 使用Lombok注解@Getter自动为dataPermissionCache生成getter方法。
     * 缓存，用于存储方法和类的数据权限注解。
     */
    private final Map<MethodClassKey, DataPermission> dataPermissionCache = new ConcurrentHashMap<>();

    @Override
    public Object invoke(@NotNull MethodInvocation methodInvocation) throws Throwable {
        // 入栈，获取当前方法的数据权限注解。
        DataPermission dataPermission = this.findAnnotation(methodInvocation);
        if (dataPermission != null) {
            DataPermissionContextHolder.add(dataPermission); // 如果存在数据权限注解，则将其添加到上下文中。
        }
        try {
            // 执行逻辑，调用原始方法。
            return methodInvocation.proceed();
        } finally {
            // 出栈，无论方法执行成功与否，都从上下文中移除数据权限注解。
            if (dataPermission != null) {
                DataPermissionContextHolder.remove();
            }
        }
    }

    private DataPermission findAnnotation(MethodInvocation methodInvocation) {
        // 1. 从缓存中获取数据权限注解。
        Method method = methodInvocation.getMethod();
        Object targetObject = methodInvocation.getThis();
        Class<?> clazz = targetObject != null ? targetObject.getClass() : method.getDeclaringClass();
        MethodClassKey methodClassKey = new MethodClassKey(method, clazz);
        DataPermission dataPermission = dataPermissionCache.get(methodClassKey);
        if (dataPermission != null) {
            return dataPermission != DATA_PERMISSION_NULL ? dataPermission : null; // 如果缓存中的数据权限注解不是空对象，则返回。
        }

        // 2.1 从方法中获取数据权限注解。
        dataPermission = AnnotationUtils.findAnnotation(method, DataPermission.class);
        // 2.2 从类上获取数据权限注解。
        if (dataPermission == null) {
            dataPermission = AnnotationUtils.findAnnotation(clazz, DataPermission.class);
        }
        // 2.3 添加到缓存中。
        dataPermissionCache.put(methodClassKey, dataPermission != null ? dataPermission : DATA_PERMISSION_NULL);
        return dataPermission; // 返回找到的数据权限注解，如果都没有找到，则返回空对象。
    }

}