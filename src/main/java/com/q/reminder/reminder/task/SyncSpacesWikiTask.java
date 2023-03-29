package com.q.reminder.reminder.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.WikiSpace;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.WikiSpaceService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.SyncSpacesWikiTask
 * @Description : 同步知识库中标准过程复制
 * @date :  2022.11.18 14:34
 */
@Component
public class SyncSpacesWikiTask implements BasicProcessor {
    @Autowired
    private WikiSpaceService spaceWikoService;
    @Autowired
    private ProjectInfoService projectInfoService;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ProcessResult processResult = new ProcessResult(true);
        try {
            int weekOfYear = DateUtil.thisWeekOfYear() - 1;
            if (weekOfYear == 0) {
                weekOfYear = 52;
            }
            String jobParam = context.getJobParams();
            if (StringUtils.isNotBlank(jobParam) && NumberUtil.isInteger(jobParam) && Integer.parseInt(jobParam) <= 52) {
                weekOfYear = Integer.parseInt(jobParam);
            }
            List<WikiSpace> wikiSpaceList = new ArrayList<>();

            LambdaQueryWrapper<WikiSpace> wikiLq = Wrappers.lambdaQuery();
            wikiLq.eq(WikiSpace::getWeekNum, weekOfYear);
            long count = spaceWikoService.count(wikiLq);
            if (count > 0) {
                log.info("当前周已复制");
                return processResult;
            }
            WikiSpace wikiSpace = spaceWikoService.getSpacesNode("wikcnXpXCgmL3E7vdbM1TiwXiGc");
            String parentTitle = wikiSpace.getTitle();
            LambdaQueryWrapper<RProjectInfo> lq = Wrappers.<RProjectInfo>lambdaQuery().select(RProjectInfo::getWikiToken, RProjectInfo::getId).isNotNull(RProjectInfo::getWikiToken);
            lq.eq(RProjectInfo::getWikiType, "0");
            List<RProjectInfo> list = projectInfoService.list(lq);
            for (RProjectInfo info : list) {
                String title = parentTitle + "-" + DateTime.now().toString("yy") + "W" + weekOfYear;
                WikiSpace space = spaceWikoService.syncSpacesWiki(info.getWikiToken(), title);
                space.setPId(info.getId());
                space.setWeekNum(weekOfYear);
                wikiSpaceList.add(space);
            }
            spaceWikoService.saveOrUpdateBatch(wikiSpaceList);
            log.error("同步知识库中标准过程复制完成");
        } catch (Exception e) {
            log.error("同步知识库中标准过程复制失败");
            processResult.setSuccess(false);
            processResult.setMsg("同步知识库中标准过程复制失败");
        }
        return processResult;
    }
}
