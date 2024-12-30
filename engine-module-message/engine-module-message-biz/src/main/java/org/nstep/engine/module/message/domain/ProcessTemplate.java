package org.nstep.engine.module.message.domain;


import lombok.Builder;
import lombok.Data;
import org.nstep.engine.module.message.process.management.BusinessProcess;

import java.util.List;

/**
 * 串联处理器形成责任链
 * <p>
 * 该类用于存储一系列的业务处理器，这些处理器将按照顺序执行，形成一个责任链。
 * 责任链模式通过将多个处理器串联起来，依次处理业务逻辑，从而实现灵活的业务流程控制。
 */
@Data
@Builder
public class ProcessTemplate {

    /**
     * 业务处理器列表
     * <p>
     * 该列表存储了一系列的业务处理器实例（实现了BusinessProcess接口的类）。
     * 这些处理器按照列表中的顺序依次执行，完成业务逻辑的处理。
     * 每个处理器负责处理其特定的业务逻辑，并可以决定是否继续执行下一个处理器。
     */
    private List<BusinessProcess> processes;

}
