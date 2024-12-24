package org.nstep.engine.framework.translate.core;

import cn.hutool.core.collection.CollUtil;
import com.fhs.core.trans.vo.VO;
import com.fhs.trans.service.impl.TransService;

import java.util.List;

/**
 * VO 数据翻译 Utils
 * 该工具类用于对 VO（值对象）数据进行翻译，尤其是那些不能通过 `@TransMethodResult` 注解自动翻译的场景。
 */
public class TranslateUtils {

    // 用于翻译的服务，初始化时注入
    private static TransService transService;

    /**
     * 初始化方法，用于注入翻译服务。
     *
     * @param transService 翻译服务
     */
    public static void init(TransService transService) {
        TranslateUtils.transService = transService;
    }

    /**
     * 批量数据翻译
     * <p>
     * 使用场景：当无法通过 `@TransMethodResult` 注解自动触发翻译时，调用此方法手动翻译数据。
     *
     * @param data 需要翻译的数据列表
     * @return 翻译后的数据列表
     */
    public static <T extends VO> List<T> translate(List<T> data) {
        // 如果数据不为空，则调用翻译服务进行批量翻译
        if (CollUtil.isNotEmpty(data)) {
            transService.transBatch(data);
        }
        // 返回翻译后的数据
        return data;
    }

}
