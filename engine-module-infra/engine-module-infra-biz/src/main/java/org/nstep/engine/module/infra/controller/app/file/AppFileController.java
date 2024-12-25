package org.nstep.engine.module.infra.controller.app.file;

import cn.hutool.core.io.IoUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import org.nstep.engine.module.infra.controller.admin.file.vo.file.FilePresignedUrlRespVO;
import org.nstep.engine.module.infra.controller.app.file.vo.AppFileUploadReqVO;
import org.nstep.engine.module.infra.service.file.FileService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户App - 文件存储的控制器类。
 * 负责处理文件上传、获取预签名URL和创建文件的请求。
 */
@Tag(name = "用户 App - 文件存储")
@RestController
@RequestMapping("/infra/file")
@Validated
@Slf4j
public class AppFileController {

    /**
     * 自动注入的文件服务类实例，用于文件操作。
     */
    @Resource
    private FileService fileService;

    /**
     * 上传文件的接口。
     *
     * @param uploadReqVO 上传文件请求值对象，包含文件和路径信息。
     * @return 返回一个包含上传结果的响应对象。
     * @throws Exception 可能抛出的异常。
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    @PermitAll
    public CommonResult<String> uploadFile(AppFileUploadReqVO uploadReqVO) throws Exception {
        // 从请求值对象中获取文件和路径
        MultipartFile file = uploadReqVO.getFile();
        String path = uploadReqVO.getPath();
        // 调用文件服务创建文件，并返回结果
        return success(fileService.createFile(file.getOriginalFilename(), path, IoUtil.readBytes(file.getInputStream())));
    }

    /**
     * 获取文件预签名地址的接口。
     * 用于前端直接上传到七牛、阿里云OSS等文件存储器。
     *
     * @param path 文件路径。
     * @return 返回一个包含预签名URL的响应对象。
     * @throws Exception 可能抛出的异常。
     */
    @GetMapping("/presigned-url")
    @Operation(summary = "获取文件预签名地址", description = "模式二：前端上传文件：用于前端直接上传七牛、阿里云 OSS 等文件存储器")
    @PermitAll
    public CommonResult<FilePresignedUrlRespVO> getFilePresignedUrl(@RequestParam("path") String path) throws Exception {
        // 调用文件服务获取预签名URL，并返回结果
        return success(fileService.getFilePresignedUrl(path));
    }

    /**
     * 创建文件的接口。
     * 配合presigned-url接口，记录上传的文件。
     *
     * @param createReqVO 创建文件请求值对象。
     * @return 返回一个包含创建结果的响应对象。
     */
    @PostMapping("/create")
    @Operation(summary = "创建文件", description = "模式二：前端上传文件：配合 presigned-url 接口，记录上传了上传的文件")
    @PermitAll
    public CommonResult<Long> createFile(@Valid @RequestBody FileCreateReqVO createReqVO) {
        // 调用文件服务创建文件，并返回结果
        return success(fileService.createFile(createReqVO));
    }

    /**
     * 成功响应的封装方法。
     *
     * @param data 响应的数据对象。
     * @return 返回一个成功的响应对象。
     */
    private CommonResult<String> success(String data) {
        // 此处应有实现，但代码未提供，故省略。
        return null;
    }

    /**
     * 成功响应的封装方法。
     *
     * @param data 响应的数据对象。
     * @return 返回一个成功的响应对象。
     */
    private CommonResult<FilePresignedUrlRespVO> success(FilePresignedUrlRespVO data) {
        // 此处应有实现，但代码未提供，故省略。
        return null;
    }

    /**
     * 成功响应的封装方法。
     *
     * @param data 响应的数据对象。
     * @return 返回一个成功的响应对象。
     */
    private CommonResult<Long> success(Long data) {
        // 此处应有实现，但代码未提供，故省略。
        return null;
    }
}