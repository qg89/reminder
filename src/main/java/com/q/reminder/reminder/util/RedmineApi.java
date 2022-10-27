package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.q.reminder.reminder.vo.*;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;

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
    public static List<Issue> queryUserByExpiredDayList(QueryVo vo) {
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
        return allIssueList;
    }

    /**
     * 查询redmine当天修改的任务，所有状态
     *
     * @param vo
     * @return
     */
    public static List<QueryRedmineVo> queryUpdateIssue(QueryVo vo) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(vo.getRedmineUrl(), vo.getApiAccessKey());
        Transport transport = mgr.getTransport();
        List<QueryRedmineVo> issues = new ArrayList<>();
        for (String project : vo.getProjects()) {
            List<RequestParam> params = List.of(
                    new RequestParam("project_id", project),
                    new RequestParam("status_id", "*"),
                    new RequestParam("assigned_to_id", "*"),
                    new RequestParam("updated_on", DateUtil.today()));
            try {
                transport.getObjectsList(Issue.class, params).stream().filter(e -> StringUtils.isNotBlank(e.getAssigneeName())).forEach(e -> {
                    QueryRedmineVo queryRedmineVo = new QueryRedmineVo();
                    queryRedmineVo.setUpdatedOn(e.getUpdatedOn());
                    queryRedmineVo.setSubject(e.getSubject());
                    queryRedmineVo.setId(e.getId() + "");
                    queryRedmineVo.setAssigneeName(e.getAssigneeName().replace(" ", ""));
                    issues.add(queryRedmineVo);
                });
            } catch (RedmineException e) {
                log.error("redmind 查询当天更新的任务异常");
            }
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

    /**
     * 创建redmine任务，并且返回需求ID
     *
     * @param featureList
     * @param definition
     * @param redmineUserMap
     */
    public static void createTask(List<FeatureListVo> featureList, DefinitionVo definition, Map<String, Integer> redmineUserMap) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(definition.getRedmineUrl(), definition.getApiAccessKey());

        Transport transport = mgr.getTransport();
        Tracker tracker = new Tracker();
        tracker.setId(2);
        tracker.setName("需求");
        Integer productAssigneeId = redmineUserMap.get(definition.getProduct());
        Integer bigDataAssigneeId = redmineUserMap.get(definition.getBigData());
        Integer appAssigneeId = redmineUserMap.get(definition.getApplication());
        Integer testAssigneeId = redmineUserMap.get(definition.getTest());
        Integer projectId = definition.getProjectId();
        Date dueDate = DateTime.now().plusDays(10).toDate();
        featureList.forEach(vo -> {
            String featureId = IdWorker.get32UUID().substring(22);
            String redmineSubject = vo.getRedmineSubject();
            String menuOne = vo.getMenuOne();
            String menuTwo = vo.getMenuTwo();
            String menuThree = vo.getMenuThree();
            if (StringUtils.isNotBlank(menuOne)) {
                redmineSubject += "-[" + menuOne + "]";
            }
            if (StringUtils.isNotBlank(menuTwo)) {
                redmineSubject += "-[" + menuTwo + "]";
            }
            if (StringUtils.isNotBlank(menuThree)) {
                redmineSubject += "-[" + menuThree + "]";
            }
            boolean check = checkRedmineTask(mgr, redmineSubject);
            if (check) {
                return;
            }

            vo.setFeatureId(featureId);
            List<CustomField> customFieldList = new ArrayList<>();
            CustomField customField = new CustomField().setName("需求ID").setId(5).setValue(featureId);
            customFieldList.add(customField);
            customField = new CustomField().setId(42).setName("需求类型").setValue("功能");
            customFieldList.add(customField);
            customField = new CustomField().setId(43).setName("原始需求").setValue(vo.getRfqId());
            customFieldList.add(customField);
            customField = new CustomField().setId(30).setName("是否需要验证").setValue("是");
            customFieldList.add(customField);

            Issue issue = new Issue()
                    .setTracker(tracker)
                    .setAssigneeId(productAssigneeId)
                    .setCreatedOn(new Date())
                    .setDueDate(dueDate)
                    .setSubject(redmineSubject)
                    // 状态 NEW
                    .setStatusId(1)
                    .setProjectId(projectId)
                    .addCustomFields(customFieldList)
                    .setDescription(vo.getDesc());
            issue.setTransport(transport);
            Issue newIssue = new Issue();
            try {
                newIssue = issue.create();
            } catch (RedmineException e) {
                log.error("创建redmine任务异常", e);
            }
            System.out.println(newIssue);
        });
    }

    /**
     * 检查是否有redmine任务
     * @param mgr
     * @param redmineSubject
     * @return
     */
    private static boolean checkRedmineTask(RedmineManager mgr, String redmineSubject) {
        Transport transport = mgr.getTransport();
        List<RequestParam> params = List.of(new RequestParam("f[]", "subject"),
                new RequestParam("op[subject]", "~"),
                new RequestParam("v[subject][]", redmineSubject));
        List<Issue> issueList = null;
        try {
            issueList = transport.getObjectsList(Issue.class, params);
        } catch (RedmineException e) {
            log.error("[保存到redmine任务]异常 ", e);
        }
        if (CollectionUtils.isEmpty(issueList)) {
            return false;
        }
        return true;
    }
}
