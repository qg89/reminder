package com.q.reminder.reminder.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lark.oapi.Client;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.WikiSpace;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.WikiSpaceService;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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
    private WikiSpaceService spaceWikoService;
    @Autowired
    private Client client;
    @Autowired
    private ProjectInfoService projectInfoService;

    @XxlJob("syncSpacesWiki")
    public ReturnT<String> syncSpacesWiki() {
        String jobParam = XxlJobHelper.getJobParam();
        List<WikiSpace> wikiSpaceList = new ArrayList<>();
        try {
            WikiSpace wikiSpace = spaceWikoService.getSpacesNode(client, "wikcnXpXCgmL3E7vdbM1TiwXiGc");
            String parentTitle = wikiSpace.getTitle();
            LambdaQueryWrapper<ProjectInfo> lq = Wrappers.<ProjectInfo>lambdaQuery().select(ProjectInfo::getWikiToken, ProjectInfo::getWikiTitle, ProjectInfo::getPId).isNotNull(ProjectInfo::getWikiToken);
            List<ProjectInfo> list = projectInfoService.list(lq);
            int weekOfYear = DateUtil.thisWeekOfYear() - 1;
            if (StringUtils.isNotBlank(jobParam) && NumberUtil.isInteger(jobParam) && Integer.parseInt(jobParam) < 52) {
                weekOfYear = Integer.parseInt(jobParam);
            }
            for (ProjectInfo info : list) {
                String title  = parentTitle + "-" + DateTime.now().toString("yy") + "W" + weekOfYear;
                WikiSpace space = spaceWikoService.syncSpacesWiki(client, info.getWikiToken(), title);
                space.setPId(Integer.valueOf(info.getPId()));
                space.setWeekNum(weekOfYear);
                wikiSpaceList.add(space);
            }
            spaceWikoService.saveOrUpdateBatch(wikiSpaceList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnT.SUCCESS;
    }
}
