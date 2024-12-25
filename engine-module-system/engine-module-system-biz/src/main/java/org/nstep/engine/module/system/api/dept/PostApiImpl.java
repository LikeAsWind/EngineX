package org.nstep.engine.module.system.api.dept;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.system.api.dept.dto.PostRespDTO;
import org.nstep.engine.module.system.dal.dataobject.dept.PostDO;
import org.nstep.engine.module.system.service.dept.PostService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 岗位 API 实现类
 * <p>
 * 该类提供了岗位相关的 RESTful API 接口，供 Feign 调用。
 * 主要功能包括获取岗位信息、验证岗位有效性等。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class PostApiImpl implements PostApi {

    @Resource
    private PostService postService; // 岗位服务，处理岗位相关的业务逻辑

    /**
     * 校验多个岗位是否有效
     *
     * @param ids 岗位 ID 列表
     * @return 校验结果的响应
     */
    @Override
    public CommonResult<Boolean> validPostList(Collection<Long> ids) {
        // 调用岗位服务进行校验
        postService.validatePostList(ids);
        // 返回校验成功的结果
        return success(true);
    }

    /**
     * 获取多个岗位的信息
     *
     * @param ids 岗位 ID 列表
     * @return 岗位信息的响应结果
     */
    @Override
    public CommonResult<List<PostRespDTO>> getPostList(Collection<Long> ids) {
        // 获取多个岗位数据对象
        List<PostDO> list = postService.getPostList(ids);
        // 将岗位数据对象列表转换为响应 DTO 列表并返回
        return success(BeanUtils.toBean(list, PostRespDTO.class));
    }

}
