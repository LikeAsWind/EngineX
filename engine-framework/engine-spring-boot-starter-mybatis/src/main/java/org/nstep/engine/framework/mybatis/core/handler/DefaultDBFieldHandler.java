package org.nstep.engine.framework.mybatis.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.nstep.engine.framework.mybatis.core.dataobject.BaseDO;
import org.nstep.engine.framework.web.core.util.WebFrameworkUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 通用参数填充实现类
 * <p>
 * 如果没有显式的对通用参数进行赋值，这里会对通用参数进行填充、赋值。
 * 该类实现了 MyBatis Plus 提供的 {@link MetaObjectHandler} 接口，用于在插入或更新数据库记录时自动填充字段。
 * 通常用于填充创建时间、更新时间、创建人、更新人等通用字段。
 */
public class DefaultDBFieldHandler implements MetaObjectHandler {

    /**
     * 插入时字段填充
     * <p>
     * 在插入数据时，如果某些字段（如创建时间、更新时间、创建人、更新人）为空，
     * 则会根据当前时间和当前登录用户的信息进行自动填充。
     *
     * @param metaObject 当前操作的对象的元数据
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 判断 metaObject 是否为 BaseDO 类型
        if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseDO baseDO) {

            // 获取当前时间
            LocalDateTime current = LocalDateTime.now();

            // 如果创建时间为空，则填充当前时间
            if (Objects.isNull(baseDO.getCreateTime())) {
                baseDO.setCreateTime(current);
            }
            // 如果更新时间为空，则填充当前时间
            if (Objects.isNull(baseDO.getUpdateTime())) {
                baseDO.setUpdateTime(current);
            }

            // 获取当前登录用户 ID
            Long userId = WebFrameworkUtils.getLoginUserId();
            // 如果当前登录用户不为空，且创建人为空，则将当前登录用户设置为创建人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getCreator())) {
                baseDO.setCreator(userId.toString());
            }
            // 如果当前登录用户不为空，且更新人为空，则将当前登录用户设置为更新人
            if (Objects.nonNull(userId) && Objects.isNull(baseDO.getUpdater())) {
                baseDO.setUpdater(userId.toString());
            }
        }
    }

    /**
     * 更新时字段填充
     * <p>
     * 在更新数据时，如果某些字段（如更新时间、更新人）为空，则会自动填充。
     *
     * @param metaObject 当前操作的对象的元数据
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 获取更新时间字段的值，如果为空，则填充当前时间
        Object modifyTime = getFieldValByName("updateTime", metaObject);
        if (Objects.isNull(modifyTime)) {
            setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        }

        // 获取更新人字段的值，如果为空，则填充当前登录用户 ID
        Object modifier = getFieldValByName("updater", metaObject);
        Long userId = WebFrameworkUtils.getLoginUserId();
        if (Objects.nonNull(userId) && Objects.isNull(modifier)) {
            setFieldValByName("updater", userId.toString(), metaObject);
        }
    }
}
