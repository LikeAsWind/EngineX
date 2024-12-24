package org.nstep.engine.framework.banner.core;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动成功后，提供文档相关的地址
 * <p>
 * 该类实现了 `ApplicationRunner` 接口，在 Spring Boot 应用启动完成后执行。
 * 它会在项目启动成功后输出一条包含文档相关地址的提示信息。
 * 使用异步线程延迟输出，确保启动信息不会被过早覆盖。
 */
@Slf4j // 使用 Lombok 的 @Slf4j 注解自动生成日志记录器
public class BannerApplicationRunner implements ApplicationRunner {

    /**
     * 在 Spring Boot 应用启动后执行
     * <p>
     * 实现了 `ApplicationRunner` 接口的 `run` 方法，在应用启动完成后输出项目启动成功的提示信息。
     * 输出的内容包括项目启动成功的标志信息，并且延迟 1 秒输出，确保其他启动日志先行显示。
     *
     * @param args 启动时的命令行参数
     */
    @Override
    public void run(ApplicationArguments args) {
        // 异步执行，确保输出不被其他日志覆盖
        ThreadUtil.execute(() -> {
            // 延迟 1 秒，保证其他日志输出先行
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
            // 输出项目启动成功的提示信息
            log.info("\n----------------------------------------------------------\n\t" +
                    "项目启动成功！\n\t" +
                    "----------------------------------------------------------"
            );
        });
    }

}
