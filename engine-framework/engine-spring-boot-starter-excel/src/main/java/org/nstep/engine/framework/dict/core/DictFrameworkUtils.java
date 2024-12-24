package org.nstep.engine.framework.dict.core;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.nstep.engine.framework.common.util.cache.CacheUtils;
import org.nstep.engine.module.system.api.dict.DictDataApi;
import org.nstep.engine.module.system.api.dict.dto.DictDataRespDTO;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 字典工具类
 * <p>
 * 提供对字典数据的缓存操作和常用功能封装，减少对字典服务的重复调用。
 */
@Slf4j
public class DictFrameworkUtils {

    // 空字典数据常量，用于处理空值场景，避免返回 null
    private static final DictDataRespDTO DICT_DATA_NULL = new DictDataRespDTO();

    // 字典数据服务接口，用于远程调用获取字典数据
    private static DictDataApi dictDataApi;

    /**
     * 缓存：缓存字典类型对应的所有字典数据列表。
     */
    private static final LoadingCache<String, List<DictDataRespDTO>> DICT_DATA_CACHE = CacheUtils.buildAsyncReloadingCache(
            Duration.ofMinutes(1L), // 缓存过期时间为 1 分钟
            new CacheLoader<>() {
                @NotNull
                @Override
                public List<DictDataRespDTO> load(@NotNull String dictType) {
                    // 通过 dictDataApi 获取字典类型对应的所有数据
                    return dictDataApi.getDictDataList(dictType).getData();
                }
            });

    /**
     * 初始化工具类，设置字典数据服务接口。
     *
     * @param dictDataApi 字典数据服务接口
     */
    public static void init(DictDataApi dictDataApi) {
        DictFrameworkUtils.dictDataApi = dictDataApi;
        log.info("[init][初始化 DictFrameworkUtils 成功]");
    }

    /**
     * 根据字典类型和字典值获取字典标签。
     *
     * @param dictType 字典类型
     * @param value    字典值
     * @return 字典标签
     */
    @SneakyThrows
    public static String getDictDataLabel(String dictType, String value) {
        return DICT_DATA_CACHE.get(dictType).stream()
                .filter(data -> Objects.equals(data.getValue(), value))
                .findFirst()
                .orElse(DICT_DATA_NULL)
                .getLabel();
    }

    /**
     * 根据字典类型和字典值获取字典标签。
     *
     * @param dictType 字典类型
     * @param value    字典值（整型）
     * @return 字典标签
     */
    public static String getDictDataLabel(String dictType, Integer value) {
        return getDictDataLabel(dictType, String.valueOf(value));
    }

    /**
     * 根据字典类型获取所有字典标签列表。
     *
     * @param dictType 字典类型
     * @return 字典标签列表
     */
    @SneakyThrows
    public static List<String> getDictDataLabelList(String dictType) {
        return DICT_DATA_CACHE.get(dictType).stream()
                .map(DictDataRespDTO::getLabel)
                .collect(Collectors.toList());
    }

    /**
     * 根据字典类型和标签解析字典值。
     *
     * @param dictType 字典类型
     * @param label    字典标签
     * @return 字典值
     */
    @SneakyThrows
    public static String parseDictDataValue(String dictType, String label) {
        return DICT_DATA_CACHE.get(dictType).stream()
                .filter(data -> Objects.equals(data.getLabel(), label))
                .findFirst()
                .orElse(DICT_DATA_NULL)
                .getValue();
    }
}
