package org.nstep.engine.framework.apilog.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作日志的操作类型
 * <p>
 * 该枚举类定义了常见的操作类型，用于标识在操作日志中记录的不同操作。每个操作类型都对应一个唯一的整数值，
 * 方便在日志中进行分类和过滤。此枚举主要用于标识 API 操作的类型，如查询、新增、修改、删除等。
 */
@Getter
@AllArgsConstructor
public enum OperateTypeEnum {

    /**
     * 查询操作
     * <p>
     * 用于标识查询操作，如获取数据、查询记录等。
     */
    GET(1),

    /**
     * 新增操作
     * <p>
     * 用于标识新增操作，如插入新数据、创建新记录等。
     */
    CREATE(2),

    /**
     * 修改操作
     * <p>
     * 用于标识修改操作，如更新已有数据、编辑记录等。
     */
    UPDATE(3),

    /**
     * 删除操作
     * <p>
     * 用于标识删除操作，如删除数据、移除记录等。
     */
    DELETE(4),

    /**
     * 导出操作
     * <p>
     * 用于标识导出操作，如导出数据到文件等。
     */
    EXPORT(5),

    /**
     * 导入操作
     * <p>
     * 用于标识导入操作，如从文件导入数据等。
     */
    IMPORT(6),

    /**
     * 其它操作
     * <p>
     * 在无法归类为以上类型时，可以选择使用其它。可以通过操作名进一步标识具体操作。
     */
    OTHER(0);

    /**
     * 操作类型的整数值
     * <p>
     * 每个操作类型都有一个唯一的整数值，方便进行存储和比较。
     */
    private final Integer type;

}
