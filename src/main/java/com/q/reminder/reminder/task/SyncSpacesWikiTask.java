package com.q.reminder.reminder.task;

import cn.hutool.core.date.DateUtil;
import com.lark.oapi.Client;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.SpaceWikoService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncSpacesWikiTask
 * @Description :
 * @date :  2022.11.18 14:34
 */
@Log4j2
@Component
public class SyncSpacesWikiTask {
    @Autowired
    private SpaceWikoService spaceWikoService;
    @Autowired
    private Client client;
    @Autowired
    private ProjectInfoService projectInfoService;

    @XxlJob("syncSpacesWiki")
    public ReturnT<String> syncSpacesWiki() {
        try {
            List<ProjectInfo> list = projectInfoService.list();
            for (ProjectInfo info : list) {
                spaceWikoService.syncSpacesWiki(client, info.getWikiToken(), info.getWikiTitle() + "-" + DateTime.now().toString("yy") + "W" + DateUtil.thisWeekOfYear());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnT.SUCCESS;
    }
}
