package com.q.reminder.reminder.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.WeeklyProjectReport;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.WeeklyProjectReportService;
import com.q.reminder.reminder.task.base.HoldayBase;
import com.q.reminder.reminder.util.WeeklyProjectFeishuUtils;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.WeeklyProjectReportHandle
 * @Description :
 * @date :  2022.11.01 14:14
 */
@Component
public class WeeklyProjectReportTask implements BasicProcessor {

    @Autowired
    private WeeklyProjectReportService weeklyProjectReportService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private FeishuProperties feishuProperties;
    @Autowired
    private HoldayBase holdayBase;
    @Autowired
    private BaseFeishu baseFeishu;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger log = context.getOmsLogger();
        ProcessResult result = new ProcessResult();
        if (holdayBase.queryHoliday()) {
            log.info("节假日放假!!!!");
            return result;
        }
        String jobParam = context.getJobParams();
        WeeklyProjectVo vo = new WeeklyProjectVo();
        vo.setAppSecret(feishuProperties.getAppSecret());
        vo.setAppId(feishuProperties.getAppId());
        vo.setFileToken(getFileToken());
        LambdaQueryWrapper<RProjectInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.isNotNull(RProjectInfo::getFolderToken).isNotNull(RProjectInfo::getProjectShortName).isNotNull(RProjectInfo::getPmKey);
        wrapper.eq(RProjectInfo::getWeeklyCopyType, "0");
        if (StringUtils.isNotBlank(jobParam)) {
            wrapper.eq(RProjectInfo::getId, jobParam);
        }
        projectInfoService.list(wrapper).forEach(projectInfo -> {
            vo.setProjectShortName(projectInfo.getProjectShortName());
            vo.setFolderToken(projectInfo.getFolderToken());
            WeeklyProjectReport projectReport = WeeklyProjectFeishuUtils.copyFile(vo);
            projectReport.setRPid(projectInfo.getId());
            weeklyProjectReportService.save(projectReport);
        });
        return result;
    }

    private String getFileToken() throws Exception {
        return baseFeishu.wiki().getNodeSpace("wikcnV143lsJnKeF2b65nSKGt1K").getObjToken();
    }
}
