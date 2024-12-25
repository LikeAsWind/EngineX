package org.nstep.engine.module.infra.api.file;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.infra.api.file.dto.FileCreateReqDTO;
import org.nstep.engine.module.infra.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * RPC 服务 - 文件接口
 * <p>
 * 该接口定义了文件相关的 RPC 服务操作，包括文件的创建、保存以及返回文件的访问路径。
 */
@FeignClient(name = ApiConstants.NAME) // TODO ：fallbackFactory =
@Tag(name = "RPC 服务 - 文件")
public interface FileApi {

    String PREFIX = ApiConstants.PREFIX + "/file";

    /**
     * 保存文件，并返回文件的访问路径
     * <p>
     * 该方法用于创建文件，默认情况下文件的名称和路径为 null，用户仅提供文件内容。
     *
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(byte[] content) {
        return createFile(null, null, content);
    }

    /**
     * 保存文件，并返回文件的访问路径
     * <p>
     * 该方法用于创建文件，用户提供文件路径和文件内容。
     *
     * @param path    文件路径
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(String path, byte[] content) {
        return createFile(null, path, content);
    }

    /**
     * 保存文件，并返回文件的访问路径
     * <p>
     * 该方法用于创建文件，用户提供文件名称、路径和文件内容。
     *
     * @param name    原文件名称
     * @param path    文件路径
     * @param content 文件内容
     * @return 文件路径
     */
    default String createFile(@RequestParam("name") String name,
                              @RequestParam("path") String path,
                              @RequestParam("content") byte[] content) {
        FileCreateReqDTO fileCreateReqDTO = new FileCreateReqDTO();
        fileCreateReqDTO.setName(name);
        fileCreateReqDTO.setPath(path);
        fileCreateReqDTO.setContent(content);
        return createFile(fileCreateReqDTO).getCheckedData();
    }

    /**
     * 保存文件，并返回文件的访问路径
     * <p>
     * 该方法用于接收文件创建请求 DTO，保存文件并返回文件路径。
     *
     * @param createReqDTO 文件创建请求 DTO
     * @return 文件路径
     */
    @PostMapping(PREFIX + "/create")
    @Operation(summary = "保存文件，并返回文件的访问路径")
    CommonResult<String> createFile(@Valid @RequestBody FileCreateReqDTO createReqDTO);

}
