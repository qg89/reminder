package com.q.reminder.reminder.handle;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.FeiShuApi;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.QueryVo;
import com.q.reminder.reminder.vo.SendVo;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.handle.RedmineUpdateHandle
 * @Description :
 * @date :  2022.10.19 13:42
 */
@Log4j2
@Component
public class RedmineUpdateHandle {

    @Value("${app.id}")
    private String appId;
    @Value("${app.secret}")
    private String appSecret;
    @Value("${redmine-config.old_url}")
    private String redmineOldUrl;
    @Value("${redmine-config.api-access-key.saiko}")
    private String apiAccessKeySaiko;

    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private UserMemberService userMemberService;

    @Scheduled(cron = "0 0/10 * * * ?")
    public void redmineUpdate10() {
        Set<String> projectIds = projectInfoService.list().stream().map(ProjectInfo::getPId).collect(Collectors.toSet());
        QueryVo vo = new QueryVo();
        vo.setProjects(projectIds);
        vo.setApiAccessKey(apiAccessKeySaiko);
        vo.setRedmineUrl(redmineOldUrl);
        List<Issue> issues = RedmineApi.queryUpdateIssue(vo);
        Map<String, List<Issue>> issueMap = issues.stream().filter(issue ->
                DateUtil.between(issue.getUpdatedOn(), new Date(), DateUnit.MINUTE) <= 10
        ).collect(Collectors.groupingBy(Issue::getAssigneeName));

        List<UserMemgerInfo> userMemgerInfos = userMemberService.list();
        Map<String, String> userNameMap = userMemgerInfos.stream().collect(Collectors.toMap(UserMemgerInfo::getUserName, UserMemgerInfo::getMemberId));

        String authorization = FeiShuApi.getSecret(appId, appSecret);
        issueMap.forEach((name, issueList) -> {
            JSONObject con = new JSONObject();
            JSONObject all = new JSONObject();
            con.put("zh_cn", all);
            all.put("title", "【任务变更提醒 (" + DateUtil.now() + ")】");
            JSONArray contentJsonArray = new JSONArray();
            all.put("content", contentJsonArray);
            for (Issue issue : issueList) {
                JSONArray subContentJsonArray = new JSONArray();
                JSONObject subject = new JSONObject();
                subject.put("tag", "text");
                subject.put("text", "\r\n任务主题: ");
                subContentJsonArray.add(subject);
                JSONObject a = new JSONObject();
                a.put("tag", "a");
                a.put("href", redmineOldUrl + "issues/" + issue.getId());
                a.put("text", issue.getSubject());
                subContentJsonArray.add(a);
                contentJsonArray.add(subContentJsonArray);
            }

            SendVo sendVo = new SendVo();
            sendVo.setContent(con.toJSONString());
            sendVo.setMemberId(userNameMap.get(name));
            try {
                FeiShuApi.sendPost(sendVo, authorization, new StringBuilder());
            } catch (IOException e) {
                log.error(e);
            }
            log.info("{},变更提醒,任务发送成功", name);
        });
    }
}
