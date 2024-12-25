package org.nstep.engine.module.system.controller.admin.dept;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.nstep.engine.framework.apilog.core.annotation.ApiAccessLog;
import org.nstep.engine.framework.common.enums.CommonStatusEnum;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.pojo.PageParam;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.framework.excel.core.util.ExcelUtils;
import org.nstep.engine.module.system.controller.admin.dept.vo.post.PostPageReqVO;
import org.nstep.engine.module.system.controller.admin.dept.vo.post.PostRespVO;
import org.nstep.engine.module.system.controller.admin.dept.vo.post.PostSaveReqVO;
import org.nstep.engine.module.system.controller.admin.dept.vo.post.PostSimpleRespVO;
import org.nstep.engine.module.system.dal.dataobject.dept.PostDO;
import org.nstep.engine.module.system.service.dept.PostService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.nstep.engine.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 岗位控制器
 * <p>
 * 该控制器处理与岗位相关的所有操作，包括创建、更新、删除岗位以及获取岗位信息等。
 * 所有操作均需要进行权限验证，确保用户具有相应的权限。
 */
@Tag(name = "管理后台 - 岗位")
@RestController
@RequestMapping("/system/post")
@Validated
public class PostController {

    @Resource
    private PostService postService;

    /**
     * 创建岗位
     * <p>
     * 根据传入的岗位创建请求数据创建一个新岗位，并返回岗位的 ID。
     *
     * @param createReqVO 岗位创建请求数据
     * @return 创建成功后返回岗位的 ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建岗位")
    @PreAuthorize("@ss.hasPermission('system:post:create')")
    public CommonResult<Long> createPost(@Valid @RequestBody PostSaveReqVO createReqVO) {
        Long postId = postService.createPost(createReqVO);
        return success(postId);
    }

    /**
     * 修改岗位
     * <p>
     * 根据传入的岗位更新请求数据更新已有岗位的信息。
     *
     * @param updateReqVO 岗位更新请求数据
     * @return 更新成功返回 true
     */
    @PutMapping("/update")
    @Operation(summary = "修改岗位")
    @PreAuthorize("@ss.hasPermission('system:post:update')")
    public CommonResult<Boolean> updatePost(@Valid @RequestBody PostSaveReqVO updateReqVO) {
        postService.updatePost(updateReqVO);
        return success(true);
    }

    /**
     * 删除岗位
     * <p>
     * 根据岗位 ID 删除岗位。
     *
     * @param id 岗位的 ID
     * @return 删除成功返回 true
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除岗位")
    @PreAuthorize("@ss.hasPermission('system:post:delete')")
    public CommonResult<Boolean> deletePost(@RequestParam("id") Long id) {
        postService.deletePost(id);
        return success(true);
    }

    /**
     * 获得岗位信息
     * <p>
     * 根据岗位 ID 获取详细的岗位信息。
     *
     * @param id 岗位的 ID
     * @return 返回岗位详细信息
     */
    @GetMapping(value = "/get")
    @Operation(summary = "获得岗位信息")
    @Parameter(name = "id", description = "岗位编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    public CommonResult<PostRespVO> getPost(@RequestParam("id") Long id) {
        PostDO post = postService.getPost(id);
        return success(BeanUtils.toBean(post, PostRespVO.class));
    }

    /**
     * 获取岗位全列表
     * <p>
     * 获取所有开启状态的岗位精简信息列表，主要用于前端的下拉选项。
     *
     * @return 返回精简的岗位列表
     */
    @GetMapping(value = {"/list-all-simple", "simple-list"})
    @Operation(summary = "获取岗位全列表", description = "只包含被开启的岗位，主要用于前端的下拉选项")
    public CommonResult<List<PostSimpleRespVO>> getSimplePostList() {
        // 获得岗位列表，只要开启状态的
        List<PostDO> list = postService.getPostList(null, Collections.singleton(CommonStatusEnum.ENABLE.getStatus()));
        // 排序后，返回给前端
        list.sort(Comparator.comparing(PostDO::getSort));
        return success(BeanUtils.toBean(list, PostSimpleRespVO.class));
    }

    /**
     * 获得岗位分页列表
     * <p>
     * 根据分页请求参数获取岗位列表，支持分页查询。
     *
     * @param pageReqVO 分页请求数据
     * @return 返回岗位的分页数据
     */
    @GetMapping("/page")
    @Operation(summary = "获得岗位分页列表")
    @PreAuthorize("@ss.hasPermission('system:post:query')")
    public CommonResult<PageResult<PostRespVO>> getPostPage(@Validated PostPageReqVO pageReqVO) {
        PageResult<PostDO> pageResult = postService.getPostPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, PostRespVO.class));
    }

    /**
     * 导出岗位数据
     * <p>
     * 根据分页请求参数导出岗位数据到 Excel 文件中。
     *
     * @param response HTTP 响应，用于输出 Excel 文件
     * @param reqVO    分页请求数据
     * @throws IOException 如果导出过程中发生 IO 异常
     */
    @GetMapping("/export")
    @Operation(summary = "岗位管理")
    @PreAuthorize("@ss.hasPermission('system:post:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void export(HttpServletResponse response, @Validated PostPageReqVO reqVO) throws IOException {
        reqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<PostDO> list = postService.getPostPage(reqVO).getList();
        // 输出 Excel 文件
        ExcelUtils.write(response, "岗位数据.xls", "岗位列表", PostRespVO.class,
                BeanUtils.toBean(list, PostRespVO.class));
    }

}
