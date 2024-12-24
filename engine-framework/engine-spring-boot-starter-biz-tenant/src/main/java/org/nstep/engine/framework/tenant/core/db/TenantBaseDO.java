package org.nstep.engine.framework.tenant.core.db;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.nstep.engine.framework.mybatis.core.dataobject.BaseDO;

/**
 * 拓展多租户的 BaseDO 基类
 * <p>
 * 该类继承自 BaseDO，增加了租户编号字段 tenantId，用于支持多租户数据的隔离。
 * 所有需要支持多租户的实体类可以继承此类，从而自动拥有 tenantId 字段。
 */
@Data // 自动生成 getter、setter、toString、equals 和 hashCode 方法
@EqualsAndHashCode(callSuper = true) // 继承父类的 equals 和 hashCode 方法
public abstract class TenantBaseDO extends BaseDO {

    /**
     * 多租户编号
     * <p>
     * 每个数据对象都带有租户编号，用于标识该数据属于哪个租户。
     * 在多租户系统中，每个租户的数据应该是隔离的，通过 tenantId 字段来实现数据隔离。
     */
    private Long tenantId;

}
