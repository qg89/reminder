package com.q.reminder.reminder.util;

import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;
import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.ResultsWrapper;
import com.taskadapter.redmineapi.internal.Transport;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.RedmineApi
 * @Description : redmine相关API
 * @date :  2022.09.26 15:14
 */
@Log4j2
public class RedmineApi {

    /**
     * 通过项目读取redmine过期任务
     *
     * @param projects       redmine项目名称
     * @param noneStatusList 排查状态
     * @param apiAccessKey   redmine密钥
     * @param redmineUrl     redmineURL
     * @return 按指派人员返回问题列表
     */
    public static Map<String, List<Issue>> queryUserList(Set<String> projects, List<String> noneStatusList, String apiAccessKey, String redmineUrl) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(redmineUrl, apiAccessKey);
        IssueManager issueManager = mgr.getIssueManager();
        List<Issue> allIssueList = new ArrayList<>();
        projects.forEach(p -> {
            try {
                List<Issue> issues = issueManager.getIssues(p, null, Include.watchers);
                List<Issue> issueList = issues.stream().filter(e -> {
                    Date dueDate = e.getDueDate();
                    return dueDate != null && new DateTime().minusDays(1).isAfter(new DateTime(dueDate)) && !noneStatusList.contains(e.getStatusName()) && StringUtils.isNotBlank(e.getAssigneeName());
                }).collect(Collectors.toList());
                allIssueList.addAll(issueList);
            } catch (RedmineException e) {
                log.error("读取redmine异常");
            }
        });
        return allIssueList.stream().collect(Collectors.groupingBy(Issue::getAssigneeName));
    }

    /**
     * 通过coverity扫描的问题，保存到redmine任务
     *
     * @param vo           redmine保存的信息
     * @param apiAccessKey redmine授权密钥
     * @param redmineUrl   redmine URL
     */
    public static void saveTask(CoverityAndRedmineSaveTaskVo vo, String apiAccessKey, String redmineUrl) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(redmineUrl, apiAccessKey);
        IssueManager issueManager = mgr.getIssueManager();
        Params params = new Params();
        params.add("f[]", "subject");
        params.add("op[subject]", "~");
        params.add("v[subject][]", "CID:[" + vo.getCid() + "]");
        ResultsWrapper<Issue> issues = null;
        try {
            issues = issueManager.getIssues(params);
        } catch (RedmineException e) {
            log.error("[保存到redmine任务]异常 ", e);
        }
        if (issues == null) {
            log.info("[保存到redmine任务] 失败,未查询相关任务");
            return;
        }
        List<Issue> results = issues.getResults();
        if (results != null && !results.isEmpty()) {
            return;
        }

        Tracker tracker = new Tracker();
        // 评审问题
        tracker.setId(38);
        tracker.setName("评审问题");
        Issue issue = new Issue()
                .setTracker(tracker)
                .setAssigneeId(vo.getAssigneeId())
                .setCreatedOn(new Date())
                .setDueDate(DateTime.now().plusDays(5).toDate())
                .setSubject(vo.getSubject())
                // 状态 NEW
                .setStatusId(1)
                .setProjectId(vo.getRedmineProjectId())
                .setDescription(vo.getDescription())
                .setParentId(vo.getParentId());
        Transport transport = mgr.getTransport();
        issue.setTransport(transport);
        try {
            issue.create();
        } catch (RedmineException e) {
            log.error("创建redmine任务异常", e);
        }
    }

    /**
     * TODO
     * 查询工时
     *
     * @param apiAccessKey
     * @param redmineUrl
     */
    private static void timeEntries(String apiAccessKey, String redmineUrl) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(redmineUrl, apiAccessKey);
        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();
        Map<String, String> params = new HashMap<>(4);
        params.put("project_id", "bug_cause_analysis");
        params.put("limit", "101");
        ResultsWrapper<TimeEntry> entries = null;
        try {
            entries = timeEntryManager.getTimeEntries(params);
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        if (entries == null) {
            return;
        }
        int totalFoundOnServer = entries.getTotalFoundOnServer();
        List<TimeEntry> results = entries.getResults();
        System.out.println(results);
    }

    /**
     * TODO
     * 查看问题
     *
     * @throws RedmineException
     */
    private static void queryIssue() throws RedmineException {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey("http://redmine-qa.mxnavi.com/", "1f905383da4f783bad92e22f430c7db0b15ae258");
        IssueManager issueManager = mgr.getIssueManager();
        Issue issueById = issueManager.getIssueById(589298, Include.attachments);
        System.out.println(issueById);
    }
}
