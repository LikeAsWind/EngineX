package org.nstep.engine.gateway.util;

import cn.hutool.core.thread.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 项目启动后，提供启动成功的提示信息
 * <p>
 * 本类实现了 Spring Boot 的 ApplicationRunner 接口，在应用启动完成后，提供项目启动成功的日志提示。
 * 使用了 Hutool 的 ThreadUtil 来延迟 1 秒后输出启动信息，确保所有初始化操作完成后再输出提示。
 */
@Component
@Slf4j
public class BannerApplicationRunner implements ApplicationRunner {

    /**
     * 应用启动后执行的操作
     * <p>
     * 延迟 1 秒后，输出项目启动成功的提示信息，确保启动日志完整并且所有初始化操作已完成。
     *
     * @param args 启动时的命令行参数
     */
    @Override
    public void run(ApplicationArguments args) {
        ThreadUtil.execute(() -> {
            // 延迟 1 秒，确保日志输出完整
            ThreadUtil.sleep(1, TimeUnit.SECONDS);
            log.info("\n----------------------------------------------------------\n\t" +
                    "项目启动成功！\n\t" +
                    "----------------------------------------------------------"
            );
        });
    }
}
