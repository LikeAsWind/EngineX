package org.nstep.engine.module.system.api.dict;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.system.api.dict.dto.DictDataRespDTO;
import org.nstep.engine.module.system.dal.dataobject.dict.DictDataDO;
import org.nstep.engine.module.system.service.dict.DictDataService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

import static org.nstep.engine.framework.common.pojo.CommonResult.success;

/**
 * 字典数据 API 实现类
 * <p>
 * 该类提供了字典数据相关的 RESTful API 接口，供 Feign 调用。
 * 主要功能包括获取字典数据、验证字典数据等。
 */
@RestController // 提供 RESTful API 接口，给 Feign 调用
@Validated // 启用参数校验
public class DictDataApiImpl implements DictDataApi {

    @Resource
    private DictDataService dictDataService; // 字典数据服务，处理字典数据相关的业务逻辑

    /**
     * 校验多个字典数据是否有效
     *
     * @param dictType 字典类型
     * @param values   字典值列表
     * @return 校验结果的响应
     */
    @Override
    public CommonResult<Boolean> validateDictDataList(String dictType, Collection<String> values) {
        // 调用字典数据服务进行校验
        dictDataService.validateDictDataList(dictType, values);
        // 返回校验成功的结果
        return success(true);
    }

    /**
     * 根据字典类型和字典值获取字典数据
     *
     * @param dictType 字典类型
     * @param value    字典值
     * @return 字典数据的响应结果
     */
    @Override
    public CommonResult<DictDataRespDTO> getDictData(String dictType, String value) {
        // 获取字典数据对象
        DictDataDO dictData = dictDataService.getDictData(dictType, value);
        // 将字典数据对象转换为响应 DTO 并返回
        return success(BeanUtils.toBean(dictData, DictDataRespDTO.class));
    }

    /**
     * 根据字典类型和标签获取字典数据
     *
     * @param dictType 字典类型
     * @param label    标签
     * @return 字典数据的响应结果
     */
    @Override
    public CommonResult<DictDataRespDTO> parseDictData(String dictType, String label) {
        // 获取字典数据对象
        DictDataDO dictData = dictDataService.parseDictData(dictType, label);
        // 将字典数据对象转换为响应 DTO 并返回
        return success(BeanUtils.toBean(dictData, DictDataRespDTO.class));
    }

    /**
     * 根据字典类型获取所有字典数据
     *
     * @param dictType 字典类型
     * @return 字典数据列表的响应结果
     */
    @Override
    public CommonResult<List<DictDataRespDTO>> getDictDataList(String dictType) {
        // 获取字典数据列表
        List<DictDataDO> list = dictDataService.getDictDataListByDictType(dictType);
        // 将字典数据对象列表转换为响应 DTO 列表并返回
        return success(BeanUtils.toBean(list, DictDataRespDTO.class));
    }

}
