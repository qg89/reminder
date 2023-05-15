package com.q.reminder.reminder.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.WikiSpace;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.WikiSpaceService;
import com.q.reminder.reminder.service.impl.FeishuService;
import lombok.RequiredArgsConstructor;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import tech.powerjob.client.PowerJobClient;
import tech.powerjob.common.response.JobInfoDTO;
import tech.powerjob.common.response.ResultDTO;
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
@RequiredArgsConstructor
public class SyncSpacesWikiTask implements BasicProcessor {
    private final WikiSpaceService spaceWikoService;
    private final ProjectInfoService projectInfoService;
    private final FeishuService feishuService;
    private final PowerJobClient client;

    @Override
    public ProcessResult process(TaskContext context) {
        OmsLogger log = context.getOmsLogger();
        ProcessResult processResult = new ProcessResult(true);
        ResultDTO<JobInfoDTO> resultDTO = client.fetchJob(context.getJobId());
        String taskName = resultDTO.getData().getJobName();
        try {
            int weekOfYear = DateUtil.thisWeekOfYear() - 1;
            if (weekOfYear == 0) {
                weekOfYear = 52;
            }
            String jobParam = context.getInstanceParams();
            if (StringUtils.isNotBlank(jobParam) && NumberUtil.isInteger(jobParam) && Integer.parseInt(jobParam) <= 52) {
                weekOfYear = Integer.parseInt(jobParam);
            }
            List<WikiSpace> wikiSpaceList = new ArrayList<>();

            LambdaQueryWrapper<WikiSpace> wikiLq = Wrappers.lambdaQuery();
            wikiLq.eq(WikiSpace::getWeekNum, weekOfYear);
            long count = spaceWikoService.count(wikiLq);
            if (count > 0) {
                log.info(taskName + "-当前周已复制");
                return processResult;
            }
            String weeklyReportSpaceId = feishuService.weeklyReportSpaceId();
            String weeklyReportToken = feishuService.weeklyReportToken();
            WikiSpace wikiSpace = spaceWikoService.getSpacesNode(weeklyReportToken);
            String parentTitle = wikiSpace.getTitle();
            LambdaQueryWrapper<RProjectInfo> lq = Wrappers.<RProjectInfo>lambdaQuery().select(RProjectInfo::getWikiToken, RProjectInfo::getId).isNotNull(RProjectInfo::getWikiToken);
            lq.eq(RProjectInfo::getWikiType, "0");
            List<RProjectInfo> list = projectInfoService.list(lq);
            for (RProjectInfo info : list) {
                String title = parentTitle + "-" + DateTime.now().toString("yy") + "W" + weekOfYear;
                WikiSpace space = spaceWikoService.syncSpacesWiki(info.getWikiToken(), title, weeklyReportToken, weeklyReportSpaceId);
                space.setPId(info.getId());
                space.setWeekNum(weekOfYear);
                wikiSpaceList.add(space);
            }
            spaceWikoService.saveOrUpdateBatch(wikiSpaceList);
            log.error(taskName + "-完成");
        } catch (Exception e) {
            throw new FeishuException(e, taskName + "-异常");
        }
        return processResult;
    }
}
