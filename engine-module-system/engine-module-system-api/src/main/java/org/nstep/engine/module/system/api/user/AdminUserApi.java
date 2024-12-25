package org.nstep.engine.module.system.api.user;

import cn.hutool.core.convert.Convert;
import com.fhs.core.trans.anno.AutoTrans;
import com.fhs.trans.service.AutoTransferable;
import feign.FeignIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.collection.CollectionUtils;
import org.nstep.engine.module.system.api.user.dto.AdminUserRespDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.nstep.engine.module.system.api.user.AdminUserApi.PREFIX;

/**
 * 管理员用户相关的 RPC 服务接口
 * <p>
 * 提供了一些用于查询和操作管理员用户的接口，包括通过用户 ID 查询用户、获取用户列表、校验用户是否有效等操作。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 管理员用户")
@AutoTrans(namespace = PREFIX, fields = {"nickname"})
public interface AdminUserApi extends AutoTransferable<AdminUserRespDTO> {

    String PREFIX = ApiConstants.PREFIX + "/user";

    /**
     * 通过用户 ID 查询用户
     * <p>
     * 根据给定的用户编号查询管理员用户的详细信息。
     *
     * @param id 用户编号
     * @return 管理员用户的响应数据
     */
    @GetMapping(PREFIX + "/get")
    @Operation(summary = "通过用户 ID 查询用户")
    @Parameter(name = "id", description = "用户编号", example = "1", required = true)
    CommonResult<AdminUserRespDTO> getUser(@RequestParam("id") Long id);

    /**
     * 通过用户 ID 查询用户下属
     * <p>
     * 查询指定用户的下属管理员用户列表。
     *
     * @param id 用户编号
     * @return 下属用户的列表
     */
    @GetMapping(PREFIX + "/list-by-subordinate")
    @Operation(summary = "通过用户 ID 查询用户下属")
    @Parameter(name = "id", description = "用户编号", example = "1", required = true)
    CommonResult<List<AdminUserRespDTO>> getUserListBySubordinate(@RequestParam("id") Long id);

    /**
     * 通过用户 ID 查询多个用户
     * <p>
     * 根据多个用户编号查询管理员用户的详细信息列表。
     *
     * @param ids 用户编号集合
     * @return 管理员用户的响应数据列表
     */
    @GetMapping(PREFIX + "/list")
    @Operation(summary = "通过用户 ID 查询用户们")
    @Parameter(name = "ids", description = "部门编号数组", example = "1,2", required = true)
    CommonResult<List<AdminUserRespDTO>> getUserList(@RequestParam("ids") Collection<Long> ids);

    /**
     * 获取指定部门的用户列表
     * <p>
     * 根据部门编号集合查询该部门下的所有管理员用户列表。
     *
     * @param deptIds 部门编号集合
     * @return 管理员用户的响应数据列表
     */
    @GetMapping(PREFIX + "/list-by-dept-id")
    @Operation(summary = "获得指定部门的用户数组")
    @Parameter(name = "deptIds", description = "部门编号数组", example = "1,2", required = true)
    CommonResult<List<AdminUserRespDTO>> getUserListByDeptIds(@RequestParam("deptIds") Collection<Long> deptIds);

    /**
     * 获取指定岗位的用户列表
     * <p>
     * 根据岗位编号集合查询该岗位下的所有管理员用户列表。
     *
     * @param postIds 岗位编号集合
     * @return 管理员用户的响应数据列表
     */
    @GetMapping(PREFIX + "/list-by-post-id")
    @Operation(summary = "获得指定岗位的用户数组")
    @Parameter(name = "postIds", description = "岗位编号数组", example = "2,3", required = true)
    CommonResult<List<AdminUserRespDTO>> getUserListByPostIds(@RequestParam("postIds") Collection<Long> postIds);

    /**
     * 获取用户 Map
     * <p>
     * 根据用户编号集合查询用户信息，并将其转换为以用户 ID 为键的 Map。
     *
     * @param ids 用户编号集合
     * @return 用户 Map，键为用户 ID，值为 `AdminUserRespDTO` 对象
     */
    default Map<Long, AdminUserRespDTO> getUserMap(Collection<Long> ids) {
        List<AdminUserRespDTO> users = getUserList(ids).getCheckedData();
        return CollectionUtils.convertMap(users, AdminUserRespDTO::getId);
    }

    /**
     * 校验用户是否有效
     * <p>
     * 校验给定的用户 ID 是否有效。无效的情况包括用户编号不存在或用户被禁用。
     *
     * @param id 用户编号
     */
    default void validateUser(Long id) {
        validateUserList(Collections.singleton(id));
    }

    /**
     * 校验用户们是否有效
     * <p>
     * 校验多个用户 ID 是否有效。
     *
     * @param ids 用户编号集合
     * @return 校验结果，返回 `true` 表示有效，`false` 表示无效
     */
    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验用户们是否有效")
    @Parameter(name = "ids", description = "用户编号数组", example = "3,5", required = true)
    CommonResult<Boolean> validateUserList(@RequestParam("ids") Collection<Long> ids);

    /**
     * 根据用户 ID 列表查询用户信息
     * <p>
     * 通过用户 ID 列表查询对应的管理员用户信息，并返回列表。
     *
     * @param ids 用户编号列表
     * @return 管理员用户的响应数据列表
     */
    @Override
    @FeignIgnore
    default List<AdminUserRespDTO> selectByIds(List<?> ids) {
        return getUserList(Convert.toList(Long.class, ids)).getCheckedData();
    }

    /**
     * 根据单个用户 ID 查询用户信息
     * <p>
     * 通过单个用户 ID 查询对应的管理员用户信息。
     *
     * @param id 用户编号
     * @return 管理员用户的响应数据
     */
    @Override
    @FeignIgnore
    default AdminUserRespDTO selectById(Object id) {
        return getUser(Convert.toLong(id)).getCheckedData();
    }

}
