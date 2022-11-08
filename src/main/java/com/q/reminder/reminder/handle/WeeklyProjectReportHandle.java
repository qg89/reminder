package com.q.reminder.reminder.handle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.WeeklyProjectReport;
import com.q.reminder.reminder.handle.base.HoldayBase;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.WeeklyProjectReportService;
import com.q.reminder.reminder.util.WeeklyProjectFeishuUtils;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.WeeklyProjectReportHandle
 * @Description :
 * @date :  2022.11.01 14:14
 */
@Component
@Log4j2
public class WeeklyProjectReportHandle {

    @Autowired
    private WeeklyProjectReportService weeklyProjectReportService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private FeishuProperties feishuProperties;
    @Autowired
    private HoldayBase holdayBase;

    @XxlJob("xxlJobWeekly")
    public ReturnT<String> weekly() {
        if (holdayBase.queryHoliday()) {
            log.info("节假日放假!!!!");
            return ReturnT.SUCCESS;
        }
        String jobParam = XxlJobHelper.getJobParam();
        WeeklyProjectVo vo = new WeeklyProjectVo();
        vo.setAppSecret(feishuProperties.getAppSecret());
        vo.setAppId(feishuProperties.getAppId());
        vo.setFileToken("doxcnj0HVWCrYvTW2uzFS4S1hLg");
        LambdaQueryWrapper<ProjectInfo> wrapper = Wrappers.<ProjectInfo>lambdaQuery().isNotNull(ProjectInfo::getFolderToken).isNotNull(ProjectInfo::getProjectShortName);
        projectInfoService.list(wrapper).forEach(projectInfo -> {
            vo.setProjectShortName(projectInfo.getProjectShortName());
            vo.setFolderToken(projectInfo.getFolderToken());
            WeeklyProjectReport projectReport = WeeklyProjectFeishuUtils.copyFile(vo);
            projectReport.setRPid(projectInfo.getPId());
            weeklyProjectReportService.save(projectReport);
        });
        return ReturnT.SUCCESS;
    }
}
