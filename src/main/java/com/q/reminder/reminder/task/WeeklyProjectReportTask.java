package com.q.reminder.reminder.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.WeeklyProjectReport;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.WeeklyProjectReportService;
import com.q.reminder.reminder.task.base.HoldayBase;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.WeeklyProjectReportHandle
 * @Description :
 * @date :  2022.11.01 14:14
 */
@Component
@RequiredArgsConstructor
public class WeeklyProjectReportTask implements BasicProcessor {

    private final WeeklyProjectReportService weeklyProjectReportService;
    private final ProjectInfoService projectInfoService;
    private final FeishuProperties feishuProperties;
    private final HoldayBase holdayBase;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ProcessResult result = new ProcessResult(true);
        try {
            if (holdayBase.queryHoliday()) {
                log.info("节假日放假!!!!");
                return result;
            }
            String jobParam = context.getInstanceParams();
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
            List<RProjectInfo> list = projectInfoService.list(wrapper);
            list.forEach(projectInfo -> {
                vo.setProjectShortName(projectInfo.getProjectShortName());
                vo.setFolderToken(projectInfo.getFolderToken());
                WeeklyProjectReport projectReport = null;
                try {
                    projectReport = BaseFeishu.cloud().space().copyFile(vo);
                } catch (Exception e) {
                    throw new FeishuException(e, context.getTaskName() + "-异常");
                }
                if (projectReport == null) {
                    return;
                }
                projectReport.setRPid(projectInfo.getId());
                weeklyProjectReportService.save(projectReport);
            });
        } catch (Exception e) {
            throw new FeishuException(e, context.getTaskName() + "-异常");
        }
        return result;
    }

    private String getFileToken() throws Exception {
        return BaseFeishu.wiki().getNodeSpace("wikcnV143lsJnKeF2b65nSKGt1K").getObjToken();
    }
}
