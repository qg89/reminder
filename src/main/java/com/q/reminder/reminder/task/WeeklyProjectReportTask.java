package com.q.reminder.reminder.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.Client;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.WeeklyProjectReport;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.WeeklyProjectReportService;
import com.q.reminder.reminder.task.base.HoldayBase;
import com.q.reminder.reminder.util.BaseFeishuJavaUtils;
import com.q.reminder.reminder.util.WeeklyProjectFeishuUtils;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
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
public class WeeklyProjectReportTask {

    @Autowired
    private WeeklyProjectReportService weeklyProjectReportService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private FeishuProperties feishuProperties;
    @Autowired
    private HoldayBase holdayBase;
    @Autowired
    private Client client;

    @XxlJob("xxlJobWeekly")
    public ReturnT<String> weekly() throws Exception {
        if (holdayBase.queryHoliday()) {
            log.info("节假日放假!!!!");
            return ReturnT.SUCCESS;
        }
        String jobParam = XxlJobHelper.getJobParam();
        WeeklyProjectVo vo = new WeeklyProjectVo();
        vo.setAppSecret(feishuProperties.getAppSecret());
        vo.setAppId(feishuProperties.getAppId());
        vo.setFileToken(getFileToken());
        LambdaQueryWrapper<ProjectInfo> wrapper = Wrappers.lambdaQuery();
        wrapper.isNotNull(ProjectInfo::getFolderToken).isNotNull(ProjectInfo::getProjectShortName).isNotNull(ProjectInfo::getPmKey);
        if (StringUtils.isNotBlank(jobParam)) {
            wrapper.eq(ProjectInfo::getId, jobParam);
        }
        projectInfoService.list(wrapper).forEach(projectInfo -> {
            vo.setProjectShortName(projectInfo.getProjectShortName());
            vo.setFolderToken(projectInfo.getFolderToken());
            WeeklyProjectReport projectReport = WeeklyProjectFeishuUtils.copyFile(vo);
            projectReport.setRPid(projectInfo.getId());
            weeklyProjectReportService.save(projectReport);
        });
        return ReturnT.SUCCESS;
    }

    private String getFileToken() throws Exception {
        return BaseFeishuJavaUtils.getNodeSpace(client, "wikcnV143lsJnKeF2b65nSKGt1K").getObjToken();
    }
}
