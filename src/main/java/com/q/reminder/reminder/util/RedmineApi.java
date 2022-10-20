package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;
import com.q.reminder.reminder.vo.QueryVo;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.RequestParam;
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
     * 通过项目读取redmine过期任务,只包含打开的状态
     *
     * @param vo
     * @return 按指派人员返回问题列表
     */
    public static Map<String, List<Issue>> queryUserByExpiredDayList(QueryVo vo) {
        List<String> noneStatusList = vo.getNoneStatusList();
        Integer expiredDay = vo.getExpiredDay();
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(vo.getRedmineUrl(), vo.getApiAccessKey());
        IssueManager issueManager = mgr.getIssueManager();
        List<Issue> allIssueList = new ArrayList<>();
        Set<String> projects = vo.getProjects();
        projects.forEach(p -> {
            try {
                List<Issue> issues = issueManager.getIssues(p, null);
                List<Issue> issueList = issues.stream().filter(e -> {
                    Date dueDate = e.getDueDate();
                    boolean filter = dueDate != null && new DateTime().minusDays(expiredDay).isAfter(new DateTime(dueDate)) && StringUtils.isNotBlank(e.getAssigneeName());
                    if (vo.getContainsStatus()) {
                        return filter && noneStatusList.contains(e.getStatusName());
                    } else {
                        return filter && !noneStatusList.contains(e.getStatusName());
                    }
                }).collect(Collectors.toList());
                allIssueList.addAll(issueList);
            } catch (RedmineException e) {
                log.error("读取redmine异常");
            }
        });
        return allIssueList.stream().collect(Collectors.groupingBy(Issue::getAssigneeName));
    }

    /**
     * 查询redmine当天修改的任务，所有状态
     *
     * @param vo
     * @return
     */
    public static List<Issue> queryUpdateIssue(QueryVo vo) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(vo.getRedmineUrl(), vo.getApiAccessKey());
        Transport transport = mgr.getTransport();
        List<Issue> issues = new ArrayList<>();
        for (String project : vo.getProjects()) {
            List<RequestParam> params = List.of(
                    new RequestParam("project_id", project),
                    new RequestParam("status_id", "*"),
                    new RequestParam("assigned_to_id", "*"),
                    new RequestParam("updated_on", DateUtil.today()));
            List<Issue> objectsList = new ArrayList<>();
            try {
                objectsList = transport.getObjectsList(Issue.class, params);
            } catch (RedmineException e) {
                log.error("redmind 查询当天更新的任务异常");
            }
            issues.addAll(objectsList);
        }
        return issues;
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
        Transport transport = mgr.getTransport();
        List<RequestParam> params = List.of(new RequestParam("f[]", "subject"),
                new RequestParam("op[subject]", "~"),
                new RequestParam("v[subject][]", "CID:[" + vo.getCid() + "]"));
        List<Issue> issueList = null;
        try {
            issueList = transport.getObjectsList(Issue.class, params);
        } catch (RedmineException e) {
            log.error("[保存到redmine任务]异常 ", e);
        }
        if (issueList == null) {
            log.info("[保存到redmine任务] 失败,未查询相关任务");
            return;
        }
        if (!issueList.isEmpty()) {
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
        issue.setTransport(transport);
        try {
            issue.create();
        } catch (RedmineException e) {
            log.error("创建redmine任务异常", e);
        }
    }
}
