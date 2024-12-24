package org.nstep.engine.framework.mybatis.core.mapper;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.interfaces.MPJBaseJoin;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.apache.ibatis.annotations.Param;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.pojo.SortablePageParam;
import org.nstep.engine.framework.common.pojo.SortingField;
import org.nstep.engine.framework.mybatis.core.util.JdbcUtils;
import org.nstep.engine.framework.mybatis.core.util.MyBatisUtils;

import java.util.Collection;
import java.util.List;

/**
 * 在 MyBatis Plus 的 {@link BaseMapper} 的基础上拓展，提供更多的能力
 * <p>
 * 1. {@link BaseMapper} 为 MyBatis Plus 的基础接口，提供基础的 CRUD 能力
 * 2. {@link MPJBaseMapper} 为 MyBatis Plus Join 的基础接口，提供连表 Join 能力
 * <p>
 * 本接口扩展了 MyBatis Plus 的基本查询功能，提供了更多的分页查询、批量操作、条件查询等方法。
 * 主要用于实现业务层对数据的操作，增强了 MyBatis Plus 默认的功能。
 */
public interface BaseMapperX<T> extends MPJBaseMapper<T> {

    /**
     * 分页查询方法，传入分页参数和查询条件。
     *
     * @param pageParam    分页参数，包含当前页和每页大小
     * @param queryWrapper 查询条件封装器
     * @return 分页结果，包含查询结果和总记录数
     */
    default PageResult<T> selectPage(SortablePageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        return selectPage(pageParam, pageParam.getSortingFields(), queryWrapper);
    }

    /**
     * 分页查询方法，传入分页参数和查询条件。
     *
     * @param pageParam    分页参数，包含当前页和每页大小
     * @param queryWrapper 查询条件封装器
     * @return 分页结果，包含查询结果和总记录数
     */
    default PageResult<T> selectPage(PageParam pageParam, @Param("ew") Wrapper<T> queryWrapper) {
        return selectPage(pageParam, null, queryWrapper);
    }

    /**
     * 分页查询方法，支持排序字段，传入分页参数、排序字段和查询条件。
     *
     * @param pageParam     分页参数，包含当前页和每页大小
     * @param sortingFields 排序字段列表
     * @param queryWrapper  查询条件封装器
     * @return 分页结果，包含查询结果和总记录数
     */
    default PageResult<T> selectPage(PageParam pageParam, Collection<SortingField> sortingFields, @Param("ew") Wrapper<T> queryWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize())) {
            List<T> list = selectList(queryWrapper);
            return new PageResult<>(list, (long) list.size());
        }

        // MyBatis Plus 查询
        IPage<T> mpPage = MyBatisUtils.buildPage(pageParam, sortingFields);
        selectPage(mpPage, queryWrapper);
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    /**
     * 分页查询，支持联表查询，传入分页参数、返回类型和查询条件。
     *
     * @param pageParam     分页参数，包含当前页和每页大小
     * @param clazz         返回类型的 Class 对象
     * @param lambdaWrapper 联表查询条件封装器
     * @return 分页结果，包含查询结果和总记录数
     */
    default <D> PageResult<D> selectJoinPage(PageParam pageParam, Class<D> clazz, MPJLambdaWrapper<T> lambdaWrapper) {
        // 特殊：不分页，直接查询全部
        if (PageParam.PAGE_SIZE_NONE.equals(pageParam.getPageSize())) {
            List<D> list = selectJoinList(clazz, lambdaWrapper);
            return new PageResult<>(list, (long) list.size());
        }

        // MyBatis Plus Join 查询
        IPage<D> mpPage = MyBatisUtils.buildPage(pageParam);
        mpPage = selectJoinPage(mpPage, clazz, lambdaWrapper);
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    /**
     * 分页查询，支持联表查询，传入分页参数、返回类型和联表查询条件。
     *
     * @param pageParam        分页参数，包含当前页和每页大小
     * @param resultTypeClass  返回类型的 Class 对象
     * @param joinQueryWrapper 联表查询条件封装器
     * @return 分页结果，包含查询结果和总记录数
     */
    default <DTO> PageResult<DTO> selectJoinPage(PageParam pageParam, Class<DTO> resultTypeClass, MPJBaseJoin<T> joinQueryWrapper) {
        IPage<DTO> mpPage = MyBatisUtils.buildPage(pageParam);
        selectJoinPage(mpPage, resultTypeClass, joinQueryWrapper);
        // 转换返回
        return new PageResult<>(mpPage.getRecords(), mpPage.getTotal());
    }

    /**
     * 根据字段和值查询单条记录。
     *
     * @param field 字段名
     * @param value 字段值
     * @return 查询到的记录，若无则返回 null
     */
    default T selectOne(String field, Object value) {
        return selectOne(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据 Lambda 表达式查询单条记录。
     *
     * @param field 字段的 Lambda 表达式
     * @param value 字段值
     * @return 查询到的记录，若无则返回 null
     */
    default T selectOne(SFunction<T, ?> field, Object value) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据多个字段和值查询单条记录。
     *
     * @param field1 第一个字段
     * @param value1 第一个字段值
     * @param field2 第二个字段
     * @param value2 第二个字段值
     * @return 查询到的记录，若无则返回 null
     */
    default T selectOne(String field1, Object value1, String field2, Object value2) {
        return selectOne(new QueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 根据多个字段和值查询单条记录。
     *
     * @param field1 第一个字段的 Lambda 表达式
     * @param value1 第一个字段值
     * @param field2 第二个字段的 Lambda 表达式
     * @param value2 第二个字段值
     * @return 查询到的记录，若无则返回 null
     */
    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 根据多个字段和值查询单条记录。
     *
     * @param field1 第一个字段的 Lambda 表达式
     * @param value1 第一个字段值
     * @param field2 第二个字段的 Lambda 表达式
     * @param value2 第二个字段值
     * @param field3 第三个字段的 Lambda 表达式
     * @param value3 第三个字段值
     * @return 查询到的记录，若无则返回 null
     */
    default T selectOne(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2, SFunction<T, ?> field3, Object value3) {
        return selectOne(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2).eq(field3, value3));
    }

    /**
     * 查询总记录数。
     *
     * @return 总记录数
     */
    default Long selectCount() {
        return selectCount(new QueryWrapper<>());
    }

    /**
     * 根据字段和值查询记录数。
     *
     * @param field 字段名
     * @param value 字段值
     * @return 符合条件的记录数
     */
    default Long selectCount(String field, Object value) {
        return selectCount(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据 Lambda 表达式查询记录数。
     *
     * @param field 字段的 Lambda 表达式
     * @param value 字段值
     * @return 符合条件的记录数
     */
    default Long selectCount(SFunction<T, ?> field, Object value) {
        return selectCount(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 查询所有记录。
     *
     * @return 所有记录
     */
    default List<T> selectList() {
        return selectList(new QueryWrapper<>());
    }

    /**
     * 根据字段和值查询记录列表。
     *
     * @param field 字段名
     * @param value 字段值
     * @return 符合条件的记录列表
     */
    default List<T> selectList(String field, Object value) {
        return selectList(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据 Lambda 表达式查询记录列表。
     *
     * @param field 字段的 Lambda 表达式
     * @param value 字段值
     * @return 符合条件的记录列表
     */
    default List<T> selectList(SFunction<T, ?> field, Object value) {
        return selectList(new LambdaQueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据字段和值集合查询记录列表。
     *
     * @param field  字段名
     * @param values 字段值集合
     * @return 符合条件的记录列表
     */
    default List<T> selectList(String field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return CollUtil.newArrayList();
        }
        return selectList(new QueryWrapper<T>().in(field, values));
    }

    /**
     * 根据 Lambda 表达式和字段值集合查询记录列表。
     *
     * @param field  字段的 Lambda 表达式
     * @param values 字段值集合
     * @return 符合条件的记录列表
     */
    default List<T> selectList(SFunction<T, ?> field, Collection<?> values) {
        if (CollUtil.isEmpty(values)) {
            return CollUtil.newArrayList();
        }
        return selectList(new LambdaQueryWrapper<T>().in(field, values));
    }

    /**
     * 根据多个字段和值查询记录列表。
     *
     * @param field1 第一个字段的 Lambda 表达式
     * @param value1 第一个字段值
     * @param field2 第二个字段的 Lambda 表达式
     * @param value2 第二个字段值
     * @return 符合条件的记录列表
     */
    default List<T> selectList(SFunction<T, ?> field1, Object value1, SFunction<T, ?> field2, Object value2) {
        return selectList(new LambdaQueryWrapper<T>().eq(field1, value1).eq(field2, value2));
    }

    /**
     * 批量插入数据，适合大量数据插入。
     *
     * @param entities 实体集合
     */
    default void insertBatch(Collection<T> entities) {
        // 特殊：SQL Server 批量插入后，获取 id 会报错，因此通过循环处理
        DbType dbType = JdbcUtils.getDbType();
        if (JdbcUtils.isSQLServer(dbType)) {
            entities.forEach(this::insert);
            CollUtil.isNotEmpty(entities);
            return;
        }
        Db.saveBatch(entities);
    }

    /**
     * 批量插入数据，适合大量数据插入，指定每批次插入的数量。
     *
     * @param entities 实体集合
     * @param size     每批次插入的数量
     * @return 是否插入成功
     */
    default Boolean insertBatch(Collection<T> entities, int size) {
        // 特殊：SQL Server 批量插入后，获取 id 会报错，因此通过循环处理
        DbType dbType = JdbcUtils.getDbType();
        if (JdbcUtils.isSQLServer(dbType)) {
            entities.forEach(this::insert);
            return CollUtil.isNotEmpty(entities);
        }
        return Db.saveBatch(entities, size);
    }

    /**
     * 批量更新数据。
     *
     * @param update 更新的数据
     */
    default void updateBatch(T update) {
        update(update, new QueryWrapper<>());
    }

    /**
     * 批量更新数据。
     *
     * @param entities 更新的数据集合
     * @return 是否更新成功
     */
    default Boolean updateBatch(Collection<T> entities) {
        return Db.updateBatchById(entities);
    }

    /**
     * 批量更新数据，指定每批次更新的数量。
     *
     * @param entities 更新的数据集合
     * @param size     每批次更新的数量
     * @return 是否更新成功
     */
    default Boolean updateBatch(Collection<T> entities, int size) {
        return Db.updateBatchById(entities, size);
    }

    /**
     * 根据字段和值删除记录。
     *
     * @param field 字段名
     * @param value 字段值
     * @return 删除的条数
     */
    default int delete(String field, String value) {
        return delete(new QueryWrapper<T>().eq(field, value));
    }

    /**
     * 根据 Lambda 表达式和字段值删除记录。
     *
     * @param field 字段的 Lambda 表达式
     * @param value 字段值
     * @return 删除的条数
     */
    default int delete(SFunction<T, ?> field, Object value) {
        return delete(new LambdaQueryWrapper<T>().eq(field, value));
    }

}
