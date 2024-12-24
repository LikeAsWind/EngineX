package org.nstep.engine.framework.mybatis.core.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.nstep.engine.framework.common.pojo.SortingField;
import org.nstep.engine.framework.mybatis.core.enums.DbTypeEnum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MyBatis 工具类
 * 提供一些与 MyBatis 相关的常用工具方法。
 */
public class MyBatisUtils {

    private static final String MYSQL_ESCAPE_CHARACTER = "`"; // MySQL 转义字符

    /**
     * 构建分页对象，使用默认排序
     * <p>
     * 通过传入的分页参数构建一个分页对象。
     *
     * @param pageParam 分页参数
     * @param <T>       分页数据的类型
     * @return 分页对象
     */
    public static <T> Page<T> buildPage(PageParam pageParam) {
        return buildPage(pageParam, null);
    }

    /**
     * 构建分页对象，支持自定义排序
     * <p>
     * 通过传入的分页参数和排序字段构建一个分页对象。
     *
     * @param pageParam     分页参数
     * @param sortingFields 排序字段集合
     * @param <T>           分页数据的类型
     * @return 分页对象
     */
    public static <T> Page<T> buildPage(PageParam pageParam, Collection<SortingField> sortingFields) {
        // 根据页码和每页数量构建分页对象
        Page<T> page = new Page<>(pageParam.getPageNo(), pageParam.getPageSize());

        // 如果有排序字段，则添加排序
        if (!CollectionUtil.isEmpty(sortingFields)) {
            page.addOrder(sortingFields.stream()
                    .map(sortingField -> SortingField.ORDER_ASC.equals(sortingField.getOrder()) ?
                            OrderItem.asc(sortingField.getField()) : OrderItem.desc(sortingField.getField()))
                    .collect(Collectors.toList()));
        }
        return page;
    }

    /**
     * 将拦截器添加到 MybatisPlusInterceptor 链中
     * <p>
     * 由于 MybatisPlusInterceptor 不支持直接添加拦截器，因此只能全量设置拦截器链。
     *
     * @param interceptor MybatisPlusInterceptor 实例
     * @param inner       需要添加的拦截器
     * @param index       插入位置
     */
    public static void addInterceptor(MybatisPlusInterceptor interceptor, InnerInterceptor inner, int index) {
        // 获取当前拦截器链中的拦截器列表
        List<InnerInterceptor> inners = new ArrayList<>(interceptor.getInterceptors());
        // 将新的拦截器添加到指定位置
        inners.add(index, inner);
        // 设置新的拦截器链
        interceptor.setInterceptors(inners);
    }

    /**
     * 获取 Table 对应的表名
     * 兼容 MySQL 转义表名 `t_xxx`
     *
     * @param table 表对象
     * @return 去除转义字符后的表名
     */
    public static String getTableName(Table table) {
        String tableName = table.getName();
        // 如果表名是 MySQL 转义字符包裹的，去掉转义字符
        if (tableName.startsWith(MYSQL_ESCAPE_CHARACTER) && tableName.endsWith(MYSQL_ESCAPE_CHARACTER)) {
            tableName = tableName.substring(1, tableName.length() - 1);
        }
        return tableName;
    }

    /**
     * 构建 Column 对象
     * <p>
     * 通过表名、表别名和字段名构建一个 Column 对象。
     *
     * @param tableName  表名
     * @param tableAlias 表别名
     * @param column     字段名
     * @return Column 对象
     */
    public static Column buildColumn(String tableName, Alias tableAlias, String column) {
        if (tableAlias != null) {
            // 如果有表别名，使用别名代替表名
            tableName = tableAlias.getName();
        }
        // 返回拼接后的 Column 对象
        return new Column(tableName + StringPool.DOT + column);
    }

    /**
     * 跨数据库的 `find_in_set` 实现
     * <p>
     * 根据当前数据库类型，生成相应的 `find_in_set` SQL 语句。
     *
     * @param column 字段名称
     * @param value  查询值（不带单引号）
     * @return `find_in_set` SQL 语句
     */
    public static String findInSet(String column, Object value) {
        // 获取当前数据库类型
        DbType dbType = JdbcUtils.getDbType();
        // 获取当前数据库类型的 `find_in_set` SQL 模板，并替换占位符
        return DbTypeEnum.getFindInSetTemplate(dbType)
                .replace("#{column}", column)
                .replace("#{value}", StrUtil.toString(value));
    }

}
