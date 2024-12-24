package org.nstep.engine.framework.mybatis.core.dataobject;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fhs.core.trans.vo.TransPojo;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体对象类
 * <p>
 * 该类实现了 {@link TransPojo} 接口，目的是为了支持 Easy-Trans 的 TransType.SIMPLE 模式，
 * 该模式下集成 MyBatis Plus 查询时使用。
 * <p>
 * 该类包含了常见的基础字段，例如创建时间、更新时间、创建者、更新者以及删除标志。
 * 这些字段通常用于数据库操作中，便于进行数据追踪和管理。
 */
@Data
@JsonIgnoreProperties(value = "transMap") // 忽略 transMap 属性，避免 Jackson 在 Spring Cache 反序列化时出现错误
public abstract class BaseDO implements Serializable, TransPojo {

    /**
     * 创建时间
     * <p>
     * 使用 MyBatis Plus 的 {@link TableField} 注解，指定该字段在插入时自动填充。
     * 该字段用于记录数据的创建时间。
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     * <p>
     * 使用 MyBatis Plus 的 {@link TableField} 注解，指定该字段在插入和更新时自动填充。
     * 该字段用于记录数据的最后更新时间。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建者
     * <p>
     * 目前使用 SysUser 的 id 编号作为创建者的标识。
     * 使用 String 类型是为了未来可能支持非数值类型的 ID，留有拓展性。
     * <p>
     * 使用 MyBatis Plus 的 {@link TableField} 注解，指定该字段在插入时自动填充，并且使用 VARCHAR 类型。
     */
    @TableField(fill = FieldFill.INSERT, jdbcType = JdbcType.VARCHAR)
    private String creator;

    /**
     * 更新者
     * <p>
     * 目前使用 SysUser 的 id 编号作为更新者的标识。
     * 使用 String 类型是为了未来可能支持非数值类型的 ID，留有拓展性。
     * <p>
     * 使用 MyBatis Plus 的 {@link TableField} 注解，指定该字段在插入和更新时自动填充，并且使用 VARCHAR 类型。
     */
    @TableField(fill = FieldFill.INSERT_UPDATE, jdbcType = JdbcType.VARCHAR)
    private String updater;

    /**
     * 是否删除
     * <p>
     * 使用 MyBatis Plus 的 {@link TableLogic} 注解，表示这是一个逻辑删除字段。
     * 如果该字段为 true，则表示该数据已被逻辑删除。
     * <p>
     * 该字段在查询时会被自动过滤掉被删除的数据。
     */
    @TableLogic
    private Boolean deleted;

}
