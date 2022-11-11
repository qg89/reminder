package com.q.reminder.reminder.task;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.q.reminder.reminder.config.FeishuProperties;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.RedmineVo;
import com.q.reminder.reminder.vo.SendVo;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.RedmineUpdateHandle
 * @Description : 每10分钟提醒一次变更的任务
 * @date :  2022.10.19 13:42
 */
@Log4j2
@Component
public class RedmineUpdateTask {

    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private UserMemberService userMemberService;
    @Autowired
    private FeishuProperties feishuProperties;

    @XxlJob("redmineUpdate10")
    public void redmineUpdate10() {
        List<ProjectInfo> projectInfoList = projectInfoService.list();
        List<RedmineVo> issues = RedmineApi.queryUpdateIssue(projectInfoList);
        Map<String, List<RedmineVo>> issueMap = issues.stream().filter(issue ->
                DateUtil.between(issue.getUpdatedOn(), new Date(), DateUnit.MINUTE) <= 10 && StringUtils.isNotBlank(issue.getAssigneeName())
        ).collect(Collectors.groupingBy(RedmineVo::getAssigneeName));

        Map<String, List<RedmineVo>> noneIssueMapByAuthorName = issues.stream().filter(issue ->
                DateUtil.between(issue.getUpdatedOn(), new Date(), DateUnit.MINUTE) <= 10 && StringUtils.isBlank(issue.getAssigneeName())
        ).collect(Collectors.groupingBy(RedmineVo::getAuthorName));

        issueMap.forEach((ik, iv) -> noneIssueMapByAuthorName.forEach((k, v) -> {
            if (ik.equals(k)) {
                iv.addAll(v);
            }
        }));
        if (CollectionUtils.isEmpty(issueMap)) {
            issueMap = noneIssueMapByAuthorName;
        }

        LambdaQueryWrapper<UserMemgerInfo> lqw = new LambdaQueryWrapper<>();
        lqw.select(UserMemgerInfo::getName, UserMemgerInfo::getMemberId);
        Map<String, String> userNameMap = userMemberService.list(lqw).stream().collect(Collectors.toMap(UserMemgerInfo::getName, UserMemgerInfo::getMemberId));

        String authorization = FeiShuApi.getSecret(feishuProperties.getAppId(), feishuProperties.getAppSecret());
        issueMap.forEach((assigneeName, issueList) -> {
            JSONObject con = new JSONObject();
            JSONObject all = new JSONObject();
            con.put("zh_cn", all);
            all.put("title", "【任务变更提醒 (" + DateUtil.now() + ")】");
            JSONArray contentJsonArray = new JSONArray();
            all.put("content", contentJsonArray);
            for (RedmineVo issue : issueList) {
                String issueAssigneeName = issue.getAssigneeName();
                JSONArray subContentJsonArray = new JSONArray();
                JSONObject subject = new JSONObject();
                subject.put("tag", "text");
                if (StringUtils.isBlank(issueAssigneeName)) {
                    subject.put("text", "\r\n任务指派人为空-请及时更新指派人: ");
                    assigneeName = assigneeName.replace(" ", "");
                } else {
                    subject.put("text", "\r\n任务主题: ");
                }
                subContentJsonArray.add(subject);
                JSONObject a = new JSONObject();
                a.put("tag", "a");
                a.put("href", issue.getRedmineUrl() + "/issues/" + issue.getRedmineId());
                a.put("text", issue.getSubject());
                subContentJsonArray.add(a);
                contentJsonArray.add(subContentJsonArray);
            }

            SendVo sendVo = new SendVo();
            sendVo.setContent(con.toJSONString());
            sendVo.setMemberId(userNameMap.get(assigneeName));
            sendVo.setAssigneeName(assigneeName);
            try {
                FeiShuApi.sendPost(sendVo, authorization, new StringBuilder());
            } catch (IOException e) {
                log.error(e);
            }
            log.info("{},变更提醒,任务发送成功", assigneeName);
        });
        log.info("变更提醒,任务执行完成!");
    }
}
