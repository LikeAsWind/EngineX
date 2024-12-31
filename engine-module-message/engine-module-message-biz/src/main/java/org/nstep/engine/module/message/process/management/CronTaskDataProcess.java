package org.nstep.engine.module.message.process.management;


import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.common.pojo.CommonResult;
import org.nstep.engine.module.message.dal.dataobject.template.TemplateDO;
import org.nstep.engine.module.message.dto.content.CronTaskContent;
import org.nstep.engine.module.message.dto.content.ProcessContent;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.nstep.engine.module.message.util.CsvFileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 对定时任务数据进行处理
 */
@Service
@Slf4j
public class CronTaskDataProcess implements BusinessProcess {

    @Autowired
    private CsvFileUtil csvFileUtil;

    @Override
    public ProcessContent process(ProcessContent context) {
        if (!(context instanceof CronTaskContent cronTaskContent)) {
            context.setIsNeedBreak(true);
            context.setResponse(
                    CommonResult.error(
                            ErrorCodeConstants.CONTEXT_BROKEN.getCode(),
                            ErrorCodeConstants.CONTEXT_BROKEN.getMsg()
                    ));

            return context;
        }
        TemplateDO messageTemplate = cronTaskContent.getMessageTemplate();
        try {
            context = csvFileUtil.readCsvBuildSendFom(messageTemplate, cronTaskContent.getSender());
        } catch (Exception e) {
            log.error("读取CSV文件失败: {}", e.getMessage());
            context.setIsNeedBreak(true);
            context.setResponse(
                    CommonResult.error0(
                            ErrorCodeConstants.READ_CSV_FAILED.getCode(),
                            ErrorCodeConstants.READ_CSV_FAILED.getMsg(),
                            e.getMessage()
                    )
            );

            CommonResult.error(
                    ErrorCodeConstants.CONTEXT_BROKEN.getCode(),
                    ErrorCodeConstants.CONTEXT_BROKEN.getMsg()

            );
        }
        return context;
    }
}
