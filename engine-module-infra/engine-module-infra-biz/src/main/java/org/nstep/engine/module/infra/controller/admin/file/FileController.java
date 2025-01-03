package org.nstep.engine.module.infra.controller.admin.file;

import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.infra.controller.admin.file.vo.file.*;
import org.nstep.engine.module.infra.dal.dataobject.file.FileDO;
import org.nstep.engine.module.infra.service.file.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;
import static org.nstep.engine.module.infra.framework.file.core.utils.FileTypeUtils.writeAttachment;

/**
 * 管理后台 - 文件存储控制器
 * <p>
 * 该类处理与文件存储相关的所有操作，包括文件上传、删除、下载、分页查询等。
 * </p>
 */
@Tag(name = "管理后台 - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class FileController {

    @Resource
    private FileService fileService;

    /**
     * 上传文件
     * <p>
     * 后端上传文件到文件存储系统。
     * </p>
     *
     * @param uploadReqVO 上传文件请求 VO
     * @return 返回文件存储路径
     * @throws Exception 如果上传过程中发生错误，则抛出异常
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件", description = "模式一：后端上传文件")
    public CommonResult<String> uploadFile(FileUploadReqVO uploadReqVO) throws Exception {
        MultipartFile file = uploadReqVO.getFile();
        String path = uploadReqVO.getPath();
        return success(fileService.createFile(file.getOriginalFilename(), path, IoUtil.readBytes(file.getInputStream())));
    }

    /**
     * 获取文件预签名地址
     * <p>
     * 该接口用于前端上传文件，返回文件存储服务的预签名 URL（如七牛、阿里云 OSS 等）。
     * </p>
     *
     * @param path 文件存储路径
     * @return 返回文件预签名地址
     * @throws Exception 如果获取预签名地址时发生错误，则抛出异常
     */
    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(@RequestParam("path") String path) throws Exception {
        return success(fileService.getFilePresignedUrl(path));
    }

    /**
     * 创建文件
     * <p>
     * 该方法用于记录前端上传的文件信息（配合 presigned-url 接口使用）。
     * </p>
     *
     * @param createReqVO 文件创建请求 VO
     * @return 返回创建的文件 ID
     */
    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
        return success(fileService.createFile(createReqVO));
    }

    /**
     * 删除文件
     * <p>
     * 该方法用于删除指定的文件。
     * </p>
     *
     * @param id 文件 ID
     * @return 返回操作是否成功
     * @throws Exception 如果删除文件时发生错误，则抛出异常
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除文件")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('infra:file:delete')")
    public CommonResult<Boolean> deleteFile(@RequestParam("id") Long id) throws Exception {
        fileService.deleteFile(id);
        return success(true);
    }

    /**
     * 下载文件
     * <p>
     * 该方法用于根据文件配置 ID 和路径下载文件内容。
     * </p>
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param configId 文件配置 ID
     * @throws Exception 如果下载文件时发生错误，则抛出异常
     */
    @GetMapping("/{configId}/get/**")
    @PermitAll
    @Operation(summary = "下载文件")
    @Parameter(name = "configId", description = "配置编号", required = true)
    public void getFileContent(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable("configId") Long configId) throws Exception {
        // 获取请求的路径
        String path = StrUtil.subAfter(request.getRequestURI(), "/get/", false);
        if (StrUtil.isEmpty(path)) {
            throw new IllegalArgumentException("结尾的 path 路径必须传递");
        }
        // 解码，解决中文路径的问题 https://gitee.com/zhijiantianya/engine/pulls/807/
        path = URLUtil.decode(path);

        // 读取内容
        byte[] content = fileService.getFileContent(configId, path);
        if (content == null) {
            log.warn("[getFileContent][configId({}) path({}) 文件不存在]", configId, path);
            response.setStatus(HttpStatus.NOT_FOUND.value());
            return;
        }
        writeAttachment(response, path, content);
    }

    /**
     * 获取文件分页
     * <p>
     * 该方法用于分页查询文件列表。
     * </p>
     *
     * @param pageVO 文件分页请求 VO
     * @return 返回文件分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "获得文件分页")
    @PreAuthorize("@ss.hasPermission('infra:file:query')")
    public CommonResult<PageResult<FileRespVO>> getFilePage(@Valid FilePageReqVO pageVO) {
        PageResult<FileDO> pageResult = fileService.getFilePage(pageVO);
        return success(BeanUtils.toBean(pageResult, FileRespVO.class));
    }
}
