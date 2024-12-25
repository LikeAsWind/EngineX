package org.nstep.engine.module.infra.api.file;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.infra.api.file.dto.FileCreateReqDTO;
import org.nstep.engine.module.infra.service.file.FileService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 文件 API 实现类
 * <p>
 * 该类实现了 FileApi 接口，提供了文件相关的 RESTful API 接口。
 * 通过 Feign 调用该接口时，可以创建文件。
 * </p>
 */
@RestController // 提供 RESTful API 接口，供 Feign 调用
@Validated // 开启 Spring 的参数校验功能
public class FileApiImpl implements FileApi {

    @Resource
    private FileService fileService; // 注入 FileService，用于处理文件相关的业务逻辑

    /**
     * 创建文件
     *
     * @param createReqDTO 文件创建请求 DTO，包含文件的名称、路径和内容
     * @return 返回创建的文件路径
     */
    @Override
    public CommonResult<String> createFile(FileCreateReqDTO createReqDTO) {
        // 调用 FileService 创建文件，并返回创建结果
        return success(fileService.createFile(createReqDTO.getName(), createReqDTO.getPath(),
                createReqDTO.getContent()));
    }

}
