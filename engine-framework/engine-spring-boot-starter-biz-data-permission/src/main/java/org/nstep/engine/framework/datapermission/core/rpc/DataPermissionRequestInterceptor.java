package org.nstep.engine.framework.datapermission.core.rpc;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.nstep.engine.framework.datapermission.core.annotation.DataPermission;
import org.nstep.engine.framework.datapermission.core.aop.DataPermissionContextHolder;

/**
 * Feign客户端请求拦截器，用于处理数据权限。
 * <p>
 * DataPermission 的 RequestInterceptor 实现类：Feign 请求时，将 {@link DataPermission} 设置到 header 中，继续透传给被调用的服务
 * <p>
 * 注意：由于 {@link DataPermission} 不支持序列化和反序列化，所以暂时只能传递它的 enable 属性
 */
public class DataPermissionRequestInterceptor implements RequestInterceptor {

    /**
     * 请求头名称，用于控制数据权限是否启用。
     */
    public static final String ENABLE_HEADER_NAME = "data-permission-enable";

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 从上下文中获取当前的数据权限注解。
        DataPermission dataPermission = DataPermissionContextHolder.get();
        // 如果数据权限注解不为空且启用标志为false，则在请求头中设置数据权限为禁用。
        if (dataPermission != null && Boolean.FALSE.equals(dataPermission.enable())) {
            requestTemplate.header(ENABLE_HEADER_NAME, "false");
        }
    }

}