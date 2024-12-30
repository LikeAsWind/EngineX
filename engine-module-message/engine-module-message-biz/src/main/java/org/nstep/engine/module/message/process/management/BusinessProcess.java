package org.nstep.engine.module.message.process.management;


import org.nstep.engine.module.message.domain.content.ProcessContent;

/**
 * 业务流程责任链
 * <p>
 * 该接口定义了责任链模式中的处理器接口，每个实现类需要实现具体的业务逻辑。
 * 通过责任链模式，可以将多个业务处理逻辑解耦并串联起来，按顺序执行。
 */
public interface BusinessProcess {

    /**
     * 责任链处理的核心方法
     *
     * @param context 责任链上下文对象，包含了责任链中需要传递的数据和处理状态。
     *                实现类可以根据上下文中的数据执行具体的业务逻辑，并更新上下文的状态。
     * @return 更新后的责任链上下文对象，供下一个处理器继续使用。
     */
    ProcessContent process(ProcessContent context);

}
