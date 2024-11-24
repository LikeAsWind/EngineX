package org.nstep.engine.module.system.job.demo;

import com.xxl.job.core.handler.annotation.XxlJob;
import org.nstep.engine.framework.tenant.core.job.TenantJob;
import org.springframework.stereotype.Component;

@Component
public class DemoJob {

    @XxlJob("demoJob")
    @TenantJob
    public void execute() {
        System.out.println("美滋滋");
    }

}
