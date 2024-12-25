package org.nstep.engine.module.system.api.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.collection.CollectionUtils;
import org.nstep.engine.module.system.api.dept.dto.PostRespDTO;
import org.nstep.engine.module.system.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 岗位 API 接口
 * <p>
 * 该接口提供了岗位信息的获取、校验等操作，供 RPC 服务使用。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 岗位")
public interface PostApi {

    String PREFIX = ApiConstants.PREFIX + "/post";

    /**
     * 校验岗位是否合法
     * <p>
     * 根据岗位编号数组校验岗位是否有效。
     *
     * @param ids 岗位编号数组
     * @return 是否有效
     */
    @GetMapping(PREFIX + "/valid")
    @Operation(summary = "校验岗位是否合法")
    @Parameter(name = "ids", description = "岗位编号数组", example = "1,2", required = true)
    CommonResult<Boolean> validPostList(@RequestParam("ids") Collection<Long> ids);

    /**
     * 获取岗位列表
     * <p>
     * 根据岗位编号数组获取多个岗位的详细信息。
     *
     * @param ids 岗位编号数组
     * @return 岗位响应 DTO 列表
     */
    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获得岗位列表")
    @Parameter(name = "ids", description = "岗位编号数组", example = "1,2", required = true)
    CommonResult<List<PostRespDTO>> getPostList(@RequestParam("ids") Collection<Long> ids);

    /**
     * 获取指定编号的岗位 Map
     * <p>
     * 获取岗位编号为指定编号的岗位，并返回一个 Map 结构，其中键为岗位编号，值为岗位响应 DTO。
     *
     * @param ids 岗位编号数组
     * @return 岗位 Map，键为岗位编号，值为岗位响应 DTO
     */
    default Map<Long, PostRespDTO> getPostMap(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return MapUtil.empty();
        }

        List<PostRespDTO> list = getPostList(ids).getData();
        return CollectionUtils.convertMap(list, PostRespDTO::getId);
    }

}
