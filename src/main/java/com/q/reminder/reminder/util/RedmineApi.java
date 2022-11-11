package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.vo.*;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.IssueRelation;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.RedmineApi
 * @Description : redmine相关API
 * @date :  2022.09.26 15:14
 */
@Log4j2
public abstract class RedmineApi {

    /**
     * 通过项目读取redmine过期任务,只包含打开的状态
     *
     * @param projectInfoList
     * @return 按指派人员返回问题列表
     */
    public static List<RedmineVo> queryUserByExpiredDayList(QueryVo vo, List<ProjectInfo> projectInfoList) {
        List<String> noneStatusList = vo.getNoneStatusList();
        Integer expiredDay = vo.getExpiredDay();
        List<RedmineVo> allIssueList = new ArrayList<>();
        projectInfoList.forEach(p -> {
            String redmineUrl = p.getRedmineUrl();
            RedmineManager mgr = RedmineManagerFactory.createWithApiKey(redmineUrl, p.getAccessKey());
            IssueManager issueManager = mgr.getIssueManager();
            try {
                issueManager.getIssues(p.getPKey(), null).stream().filter(e -> {
                    Date dueDate = e.getDueDate();
                    boolean filter = dueDate != null && new DateTime().minusDays(expiredDay).isAfter(new DateTime(dueDate)) && StringUtils.isNotBlank(e.getAssigneeName());
                    if (vo.getContainsStatus()) {
                        return filter && noneStatusList.contains(e.getStatusName());
                    } else {
                        return filter && !noneStatusList.contains(e.getStatusName());
                    }
                }).forEach(e -> {
                    String assigneeName = e.getAssigneeName();
                    RedmineVo queryRedmineVo = new RedmineVo();
                    queryRedmineVo.setDueDate(e.getDueDate());
                    queryRedmineVo.setSubject(e.getSubject());
                    queryRedmineVo.setRedmineUrl(redmineUrl);
                    queryRedmineVo.setUpdatedOn(e.getUpdatedOn());
                    queryRedmineVo.setRedmineId(String.valueOf(e.getId()));
                    queryRedmineVo.setAuthorName(e.getAuthorName());
                    if (StringUtils.isNotBlank(assigneeName)) {
                        queryRedmineVo.setAssigneeName(assigneeName);
                    }
                    queryRedmineVo.setStatusName(e.getStatusName());
                    queryRedmineVo.setAssigneeId(String.valueOf(e.getAssigneeId()));
                    queryRedmineVo.setProjectName(e.getProjectName());
                    allIssueList.add(queryRedmineVo);
                });
            } catch (RedmineException e) {
                log.error("Redmine-读取任务异常");
            }
        });
        return allIssueList;
    }

    /**
     * 查询redmine当天修改的任务，所有状态
     *
     * @param projectInfoList
     * @return
     */
    public static List<RedmineVo> queryUpdateIssue(List<ProjectInfo> projectInfoList) {
        List<RedmineVo> issues = new ArrayList<>();
        for (ProjectInfo project : projectInfoList) {
            RedmineManager mgr = RedmineManagerFactory.createWithApiKey(project.getRedmineUrl(), project.getAccessKey());
            Transport transport = mgr.getTransport();
            List<RequestParam> params = List.of(
                    new RequestParam("project_id", project.getPId()),
                    new RequestParam("status_id", "*"),
//                    new RequestParam("assigned_to_id", "*"),
                    new RequestParam("updated_on", DateUtil.today()));
            try {
                transport.getObjectsList(Issue.class, params).forEach(e -> {
                    String assigneeName = e.getAssigneeName();
                    RedmineVo queryRedmineVo = new RedmineVo();
                    queryRedmineVo.setUpdatedOn(e.getUpdatedOn());
                    queryRedmineVo.setSubject(e.getSubject());
                    queryRedmineVo.setRedmineId(e.getId() + "");
                    queryRedmineVo.setAuthorName(e.getAuthorName());
                    if (StringUtils.isNotBlank(assigneeName)) {
                        queryRedmineVo.setAssigneeName(assigneeName.replace(" ", ""));
                    }
                    queryRedmineVo.setRedmineUrl(project.getRedmineUrl());
                    issues.add(queryRedmineVo);
                });
            } catch (RedmineException e) {
                log.error("Redmine-查询当天更新的任务异常");
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
            log.error("Redmine-[保存评审问题任务]异常 ", e);
            return;
        }
        if (issueList == null) {
            log.info("Redmine-[保存评审问题任务] 失败,未查询相关任务");
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
            log.error("Redmine-创建评审问题任务异常", e);
        }
    }

    /**
     * 创建redmine任务，并且返回需求ID
     *
     * @param featureList
     * @param definition
     * @param redmineUserMap
     * @param pKey
     */
    public static void createTask(List<FeatureListVo> featureList, DefinitionVo definition, Map<String, Integer> redmineUserMap, String pKey) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(definition.getRedmineUrl() + "/projects/" + pKey, definition.getApiAccessKey());

        Transport transport = mgr.getTransport();
        Tracker tracker = new Tracker();
        tracker.setId(2);
        tracker.setName("需求");
        Integer productAssigneeId = redmineUserMap.get(definition.getProduct());
        Integer bigDataAssigneeId = redmineUserMap.get(definition.getBigData());
        Integer backendAssigneeId = redmineUserMap.get(definition.getBackend());
        Integer testAssigneeId = redmineUserMap.get(definition.getTest());
        Integer algorithmAssigneeId = redmineUserMap.get(definition.getAlgorithm());
        Integer frontAssigneeId = redmineUserMap.get(definition.getFront());
        Integer projectId = definition.getProjectId();

        for (FeatureListVo vo : featureList) {
            String devT = vo.getDevTime();
            String prodT = vo.getProdTime();
            Date devTime = DateTime.now().plusDays(10).toDate();
            Date prodTime = devTime;
            Date today = DateTime.parse(DateUtil.today()).toDate();
            if (StringUtils.isNotBlank(devT)) {
                devTime = DateTime.parse(devT.split("-")[0], DateTimeFormat.forPattern("yyMMdd")).toDate();
            }
            if (StringUtils.isNotBlank(prodT)) {
                prodTime = DateTime.parse(prodT.split("-")[0], DateTimeFormat.forPattern("yyMMdd")).toDate();
            }
            if (prodTime.before(today)) {
                prodTime = devTime;
            }
            if (devTime.after(prodTime)) {
                devTime = prodTime;
            }
            String featureId = IdWorker.get32UUID().substring(22);
            String redmineSubject = "";
            String menuOne = vo.getMenuOne();
            String menuTwo = vo.getMenuTwo();
            String menuThree = vo.getMenuThree();
            String front = vo.getFront();
            String backend = vo.getBackend();
            String bigData = vo.getBigData();
            String algorithm = vo.getAlgorithm();
            String test = vo.getTest();
            String desc = vo.getDesc();
            String featureType = vo.getFeatureType();
            String parentFeatureId = vo.getParentFeatureId();
            if (StringUtils.isNotBlank(menuOne)) {
                redmineSubject += "[" + menuOne + "]";
            }
            if (StringUtils.isNotBlank(menuTwo)) {
                redmineSubject += "-[" + menuTwo + "]";
            }
            if (StringUtils.isNotBlank(menuThree)) {
                redmineSubject += "-[" + menuThree + "]";
            }
            redmineSubject += "-[" + featureId + "]";

            boolean check = checkRedmineTask(mgr, redmineSubject);
            if (check) {
                return;
            }

            List<CustomField> customFieldList = new ArrayList<>();
            CustomField customField = new CustomField().setName("需求ID").setId(5).setValue(featureId);
            customFieldList.add(customField);
            customField = new CustomField().setId(42).setName("需求类型").setValue("功能");
            customFieldList.add(customField);
            customField = new CustomField().setId(30).setName("是否需要验证").setValue("是");
            customFieldList.add(customField);

            Issue issue = new Issue()
                    .setTracker(tracker)
                    .setAssigneeId(productAssigneeId)
                    .setCreatedOn(new Date())
                    .setDueDate(prodTime)
                    .setSubject(redmineSubject)
                    // 状态 NEW
                    .setStatusId(1)
                    .setProjectId(projectId)
                    .addCustomFields(customFieldList)
                    .setDescription(desc);
            issue.setTransport(transport);
            Issue newIssue;
            try {
                newIssue = issue.create();
            } catch (RedmineException e) {
                log.error("Redmine-创建需求任务异常", e);
                return;
            }
            Integer newIssueId = newIssue.getId();
            vo.setRedmineId(String.valueOf(newIssueId));
            vo.setFeatureId(featureId);
            vo.setRedmineSubject(redmineSubject);
            log.info("Redmine-创建需求任务成功, redmineId: {}, 主题:[{}]", newIssueId, redmineSubject);
            Tracker dep = new Tracker().setId(7).setName("开发");
            newIssue.setDueDate(devTime);
            if (StringUtils.isNotBlank(front) && frontAssigneeId != null) {
                createSubTask(newIssue, frontAssigneeId, dep, newIssueId, redmineSubject, "前端");
            }
            if (StringUtils.isNotBlank(backend) && backendAssigneeId != null) {
                createSubTask(newIssue, backendAssigneeId, dep, newIssueId, redmineSubject, "后端");
            }
            if (StringUtils.isNotBlank(bigData) && bigDataAssigneeId != null) {
                createSubTask(newIssue, bigDataAssigneeId, dep, newIssueId, redmineSubject, "大数据");
            }
            if (StringUtils.isNotBlank(algorithm) && algorithmAssigneeId != null) {
                createSubTask(newIssue, algorithmAssigneeId, dep, newIssueId, redmineSubject, "算法");
            }
            if (StringUtils.isNotBlank(test) && testAssigneeId != null) {
                Tracker tra = new Tracker()
                        .setName("测试")
                        .setId(8);
                newIssue.setStartDate(devTime);
                newIssue.setDueDate(prodTime);
                createSubTask(newIssue, testAssigneeId, tra, newIssueId, redmineSubject, "测试用例");
                createSubTask(newIssue, testAssigneeId, tra, newIssueId, redmineSubject, "测试执行");
            }
            if (StringUtils.isNotBlank(parentFeatureId) && StringUtils.isNotBlank(featureType) && "变更".equals(featureType)) {
                Integer parentId = getParentId(transport, parentFeatureId);
                createParentFeatureId(parentId, newIssueId, definition);
            }
        }
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
        List<Issue> issueList;
        try {
            issueList = transport.getObjectsList(Issue.class, params);
        } catch (RedmineException e) {
            log.error("Redmine-[保存任务]异常 ", e);
            return false;
        }
        if (CollectionUtils.isEmpty(issueList)) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param issueParent
     * @param assigneeId
     * @param tracker
     * @param parentIssueId
     * @param subject
     * @param type
     */
    private static void createSubTask(Issue issueParent, Integer assigneeId, Tracker tracker, Integer parentIssueId, String subject, String type) {
        Issue issue;
        try {
            issue = issueParent.setTracker(tracker)
                    .setAssigneeId(assigneeId)
                    .setParentId(parentIssueId)
                    .setSubject(type + "-" + subject)
                    .create();
        } catch (RedmineException e) {
            log.error("Redmine-创建{}子任务异常 {}", type, e);
            return;
        }
        log.info("Redmine-创建[{}]任务成功, redmineId: {}, 主题:[{}]", type, issue.getId(), issue.getSubject());
    }

    /**
     * 创建关联任务
     *
     * @param parentId
     * @param issueId
     * @param definition
     */
    private static void createParentFeatureId(Integer parentId, Integer issueId, DefinitionVo definition) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(definition.getRedmineUrl(), definition.getApiAccessKey());
        Transport transport = mgr.getTransport();
        List<IssueRelation> relations = List.of(new IssueRelation(transport, issueId, parentId, "relates"));
        try {
            Issue issue = mgr.getIssueManager().getIssueById(issueId).addRelations(relations);
            issue.update();
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        log.info("更新关联任务成功！");
    }

    /**
     * 根据需求ID 查询redmine任务
     *
     * @param transport
     * @param parentFeatureId
     * @return
     */
    private static Integer getParentId(Transport transport, String parentFeatureId) {
        Collection<RequestParam> list = List.of(
                new RequestParam("f[]", "cf_5"),
                new RequestParam("op[cf_5]", "="),
                new RequestParam("v[cf_5][]", parentFeatureId)
        );
        try {
            Issue issue = transport.getObjectsList(Issue.class, list).stream().filter(e -> (2 == e.getTracker().getId())).toList().stream().findAny().get();
            return issue.getId();
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        String s = DateTime.parse("221110", DateTimeFormat.forPattern("yyMMdd")).toString("yyyyMMdd");
        System.out.println(s);
    }
}
