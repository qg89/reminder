package com.q.reminder.reminder.task;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.WUserTimes;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.WUserTimesService;
import com.q.reminder.reminder.util.RedmineApi;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncProjcetUserTimeTask
 * @Description :
 * @date :  2022.12.28 11:34
 */
@Log4j2
@Component
public class SyncProjcetUserTimeTask {
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private WUserTimesService wUserTimesService;

    @XxlJob("syncProjcetUserTimeTask")
    public ReturnT<String> syncProjcetUserTimeTask() throws RedmineException {
        Date startDate = DateUtil.beginOfMonth(new Date()).toJdkDate();
        Date endDate = null;
        String pKey = "";
        String jobParam = XxlJobHelper.getJobParam();
        JSONObject json = JSONObject.parseObject(jobParam);
        if (json.containsKey("startDate")) {
            startDate = json.getDate("startDate");
        }
        if (json.containsKey("endDate")) {
            endDate = json.getDate("endDate");
        }
        LambdaQueryWrapper<ProjectInfo> lq = Wrappers.lambdaQuery();
        if (json.containsKey("pKey")) {
            pKey = json.getString("pKey");
            lq.eq(ProjectInfo::getPKey, pKey);
        }
        List<ProjectInfo> projectList = projectInfoService.list(lq);
        List<WUserTimes> userTimesData = new ArrayList<>();
        for (ProjectInfo info : projectList) {
            String pId = info.getPId();
            Date finalStartDate = startDate;
            Date finalEndDate = endDate;
            if (finalEndDate != null) {
                info.setStartDay(startDate);
            }
            List<TimeEntry> timeEntries = RedmineApi.queryTimes(info).stream().filter(e -> {
                Date spentOn = e.getSpentOn();
                boolean isDate = true;
                if (finalStartDate != null) {
                    isDate = spentOn.after(DateUtil.offsetSecond(finalStartDate, -1));
                }
                if (finalEndDate != null) {
                    isDate = isDate && spentOn.before(DateUtil.offsetDay(finalEndDate, 1));
                }
                return isDate;
            }).toList();
            Map<String, Map<String, Double>> map = timeEntries.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getUserId()), Collectors.groupingBy(e -> new DateTime(e.getSpentOn()).toString("yyyy-MM-dd"),
                    Collectors.summingDouble(e -> BigDecimal.valueOf(e.getHours()).setScale(2, RoundingMode.HALF_UP).doubleValue()))));
            map.forEach((userId, v) -> {
                v.forEach((days, h) -> {
                    WUserTimes times = new WUserTimes();
                    times.setPId(pId);
                    times.setDay(days);
                    times.setHouses(BigDecimal.valueOf(h));
                    times.setUserId(userId);
                    userTimesData.add(times);
                    LambdaQueryWrapper<WUserTimes> query = Wrappers.lambdaQuery();
                    query.eq(WUserTimes::getPId, pId);
                    query.eq(WUserTimes::getUserId, userId);
                    query.eq(true, WUserTimes::getDay, days);
                    wUserTimesService.remove(query);
                });
            });
        }
        wUserTimesService.saveOrUpdateBatch(userTimesData);
        return new ReturnT<>(null);
    }
}
