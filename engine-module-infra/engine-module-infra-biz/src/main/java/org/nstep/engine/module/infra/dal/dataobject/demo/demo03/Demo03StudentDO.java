package org.nstep.engine.module.infra.dal.dataobject.demo.demo03;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.nstep.engine.framework.mybatis.core.dataobject.BaseDO;

import java.time.LocalDateTime;

/**
 * 学生 DO
 */
@TableName("demo03_student")
@KeySequence("demo03_student_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Demo03StudentDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 名字
     */
    private String name;
    /**
     * 性别
     * <p>
     * 枚举
     */
    private Integer sex;
    /**
     * 出生日期
     */
    private LocalDateTime birthday;
    /**
     * 简介
     */
    private String description;

}