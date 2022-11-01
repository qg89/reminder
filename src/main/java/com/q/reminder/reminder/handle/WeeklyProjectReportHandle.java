package com.q.reminder.reminder.handle;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.WeeklyProjectReport;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.WeeklyProjectReportService;
import com.q.reminder.reminder.util.WeeklyProjectFeishuUtils;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
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
    @Value("${app.id}")
    private String appId;
    @Value("${app.secret}")
    private String appSecret;

    @Autowired
    private WeeklyProjectReportService weeklyProjectReportService;
    @Autowired
    private ProjectInfoService projectInfoService;

    @XxlJob("xxlJobWeekly")
    public ReturnT<String> weekly() {
        String jobParam = XxlJobHelper.getJobParam();
        WeeklyProjectVo vo = new WeeklyProjectVo();
        vo.setAppSecret(appSecret);
        vo.setAppId(appId);
        vo.setFileToken("doxcnj0HVWCrYvTW2uzFS4S1hLg");
        LambdaQueryWrapper<ProjectInfo> wrapper = Wrappers.<ProjectInfo>lambdaQuery().isNotNull(ProjectInfo::getFolderToken).isNotNull(ProjectInfo::getProjectShortName);
        projectInfoService.list(wrapper).forEach(projectInfo -> {
            vo.setProjectSshortName(projectInfo.getProjectShortName());
            vo.setFolderToken(projectInfo.getFolderToken());
            WeeklyProjectReport projectReport = WeeklyProjectFeishuUtils.copyFile(vo);
            projectReport.setRPid(projectInfo.getPId());
            weeklyProjectReportService.save(projectReport);
        });
        return ReturnT.SUCCESS;
    }
}
