package com.q.reminder.reminder.util;

import com.q.reminder.reminder.entity.CoverityVo;
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
 * @Description :
 * @date :  2022.09.26 15:14
 */
@Log4j2
public class RedmineApi {
    private static final String REDMINE_URL = "http://redmine-qa.mxnavi.com/";
    private static final String API_ACCESS_KEY = "1f905383da4f783bad92e22f430c7db0b15ae258";

    public static void main(String[] args) throws RedmineException {
        final Integer projectId = 10077;
        final Integer viewId = 10738;
//        readCoverity(projectId, viewId,API_ACCESS_KEY, REDMINE_URL);
//        time_entries(API_ACCESS_KEY, REDMINE_URL);
        queryIssue();
    }

    private static void readCoverity(Integer projectId, Integer viewId, String apiAccessKey, String redmineUrl) {
        List<CoverityVo> coverityVoList = CoverityApi.queryHightMidQ("E6E6E8432545DE9FB6A106BA6B47AB98", projectId, viewId);
        if (coverityVoList == null || coverityVoList.isEmpty()) {
            log.info("coverity 返回结果为空");
            return;
        }
        coverityVoList.forEach(e -> {
            String type = e.getDisplayType();
            String cid = String.valueOf(e.getCid());
            String content = "类型:" + type + "," + "CID:" + cid + "\r\n" +
                    "类别:" + e.getDisplayCategory() + "\r\n" +
                    "文件路径:" + e.getDisplayFile() + "\r\n" +
                    "行数:" + e.getLineNumber();
            String subject = type + "-" + cid;
            try {
                saveTask(subject, content, cid, apiAccessKey, redmineUrl);
            } catch (RedmineException ex) {
                ex.printStackTrace();
            }
        });
    }


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

    public static void saveTask(String subject, String content, String cid, String apiAccessKey, String redmineUrl) throws RedmineException {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(redmineUrl, apiAccessKey);
        IssueManager issueManager = mgr.getIssueManager();
        Params params = new Params();
        params.add("f[]", "subject");
        params.add("op[subject]", "~");
        params.add("v[subject][]", cid);
        ResultsWrapper<Issue> issues = issueManager.getIssues(params);
        List<Issue> results = issues.getResults();
        if (results != null && !results.isEmpty()) {
            return;
        }

        Tracker tracker = new Tracker();
        tracker.setId(38);
        tracker.setName("评审问题");
        Issue issue = new Issue()
                .setTracker(tracker)
                .setAssigneeId(2751)
                .setCreatedOn(new Date())
                .setDueDate(DateTime.now().plusDays(5).toDate())
                .setSubject(subject)
                .setStatusId(1)
                .setProjectId(1806)
                .setDescription(content);
        Transport transport = mgr.getTransport();
        issue.setTransport(transport);
        issue.create();
//        issueManager.createIssue(issue);

    }

    public static void time_entries(String apiAccessKey, String redmineUrl) throws RedmineException {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(redmineUrl, apiAccessKey);
        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();
        Map<String, String> params = new HashMap<>(4);
        params.put("project_id", "bug_cause_analysis");
        params.put("limit", "101");
        ResultsWrapper<TimeEntry> entries = timeEntryManager.getTimeEntries(params);
        int totalFoundOnServer = entries.getTotalFoundOnServer();
        List<TimeEntry> results = entries.getResults();
        System.out.println(results);
    }

    public static void queryIssue() throws RedmineException {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey("http://redmine-buganalysis-qa.mxnavi.com/", "e6ca9498eba37aa49a8a70c6415a8bd1667893db");
        IssueManager issueManager = mgr.getIssueManager();
        Issue issueById = issueManager.getIssueById(3140, Include.attachments);
        System.out.println(issueById);
    }
}
