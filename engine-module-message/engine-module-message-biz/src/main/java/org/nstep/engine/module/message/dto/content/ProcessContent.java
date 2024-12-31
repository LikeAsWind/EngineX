package org.nstep.engine.module.message.dto.content;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.nstep.engine.framework.common.pojo.CommonResult;


/**
 * 责任链上下文对象
 * <p>
 * 该类用于在责任链模式中传递处理过程中的上下文信息。它包含是否需要中断责任链的标志，以及处理结果。
 * 通过该对象，责任链中的每个处理器可以共享处理状态，决定是否继续执行责任链中的下一个处理器。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessContent {

    /**
     * 是否需要中断责任链
     * <p>
     * 当为true时，表示责任链中的处理过程需要被中断，后续的处理器将不再执行。
     * 默认为false，表示责任链继续执行。
     */
    private Boolean isNeedBreak = false;

    /**
     * 处理结果
     * <p>
     * 存储当前处理过程的结果，可以是成功、失败或其他业务逻辑处理的返回结果。
     * 该字段是通用的，可以根据实际需要传递不同类型的结果。
     */
    private CommonResult<?> response;

}
