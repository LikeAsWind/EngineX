package org.nstep.engine.framework.ip.core;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.nstep.engine.framework.ip.core.enums.AreaTypeEnum;

import java.util.List;

/**
 * 区域节点，包括国家、省份、城市、地区等信息
 * <p>
 * 数据可见 resources/area.csv 文件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
// Lombok注解，生成`toString`方法时排除`parent`属性，避免循环引用。
@ToString(exclude = {"parent"}) // 参见 https://gitee.com/enginecode/engine-cloud-mini/pulls/2 原因
public class Area {

    /**
     * 编号 - 全球，即根目录
     */
    public static final Integer ID_GLOBAL = 0;
    /**
     * 编号 - 中国
     */
    public static final Integer ID_CHINA = 1;

    /**
     * 编号
     */
    private Integer id;
    /**
     * 名字
     */
    private String name;
    /**
     * 类型
     * <p>
     * 枚举 {@link AreaTypeEnum}
     */
    private Integer type;

    /**
     * 父区域节点，使用@JsonManagedReference注解防止JSON序列化时的循环引用。
     * 父节点
     */
    @JsonManagedReference
    private Area parent;
    /**
     * 子区域节点列表，使用@JsonBackReference注解防止JSON序列化时的循环引用。
     * 子节点
     */
    @JsonBackReference
    private List<Area> children;

}
