package com.q.reminder.reminder.task;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lark.oapi.service.im.v1.enums.CreateMessageReceiveIdTypeEnum;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.UserMemgerInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.UserMemberService;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.MessageVo;
import com.q.reminder.reminder.vo.RedmineVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

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
@Component
public class RedmineUpdateTask implements BasicProcessor {

    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private UserMemberService userMemberService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger log = context.getOmsLogger();
        List<RProjectInfo> projectInfoList = projectInfoService.listAll().stream().filter(e -> StringUtils.isNotBlank(e.getPmKey())).toList();
        List<RedmineVo> issues = RedmineApi.queryUpdateIssue(projectInfoList);
        Map<String, List<RedmineVo>> issueMap = issues.stream().filter(issue ->
                DateUtil.between(issue.getUpdatedOn(), new Date(), DateUnit.MINUTE) <= 10 && StringUtils.isNotBlank(issue.getAssigneeName())
        ).collect(Collectors.groupingBy(RedmineVo::getAssigneeName));

        Map<String, List<RedmineVo>> noneIssueMapByAuthorName = issues.stream().filter(issue ->
                /*DateUtil.between(issue.getUpdatedOn(), new Date(), DateUnit.MINUTE) <= 10 &&*/ StringUtils.isBlank(issue.getAssigneeName())
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
        lqw.eq(UserMemgerInfo::getResign, "0");
        Map<String, String> userNameMap = userMemberService.list(lqw).stream().collect(Collectors.toMap(UserMemgerInfo::getName, UserMemgerInfo::getMemberId));

        issueMap.forEach((assigneeName, issueList) -> {
            sendContexToMember(log, userNameMap, assigneeName, issueList);
        });
        log.info("变更提醒,任务执行完成!");
        return new ProcessResult(true);
    }

    private static void sendContexToMember(OmsLogger log, Map<String, String> userNameMap, String assigneeName, List<RedmineVo> issueList) {
        JSONObject con = new JSONObject();
        JSONObject all = new JSONObject();
        con.put("zh_cn", all);
        JSONArray contentJsonArray = new JSONArray();
        all.put("content", contentJsonArray);
        for (RedmineVo issue : issueList) {
            String issueAssigneeName = issue.getAssigneeName();
            JSONArray subContentJsonArray = new JSONArray();
            JSONObject subject = new JSONObject();
            subject.put("tag", "text");
            if (StringUtils.isBlank(issueAssigneeName)) {
                all.put("title", "【任务指派人为空提醒 (" + DateUtil.now() + ")】");
                subject.put("text", "\r\n请及时更新指派人: ");
                assigneeName = assigneeName.replace(" ", "");
                log.info("[redmine]-变更提醒, 任务指派人为空, 任务主题: {}, 任务ID: {}", assigneeName, issue.getRedmineId());
            } else {
                all.put("title", "【任务变更提醒 (" + DateUtil.now() + ")】");
                subject.put("text", "\r\n任务主题: ");
                log.info("[redmine]-变更提醒, 任务主题: {}, 任务ID: {}", issue.getSubject(), issue.getRedmineId());
            }
            subContentJsonArray.add(subject);
            JSONObject a = new JSONObject();
            a.put("tag", "a");
            a.put("href", issue.getRedmineUrl() + "/issues/" + issue.getRedmineId());
            a.put("text", issue.getSubject());
            subContentJsonArray.add(a);
            contentJsonArray.add(subContentJsonArray);
        }

        MessageVo vo = new MessageVo();
        vo.setMsgType("post");
        vo.setReceiveId(userNameMap.get(assigneeName));
        vo.setReceiveIdTypeEnum(CreateMessageReceiveIdTypeEnum.OPEN_ID);
        vo.setContent(con.toJSONString());
        boolean send = BaseFeishu.message().sendContent(vo);
        log.info("[redmine]-变更提醒, 发送给: {}, 状态: {} ！", assigneeName, send);
    }
}
