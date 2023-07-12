package com.q.reminder.reminder.util;

import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.enums.CustomFieldsEnum;
import com.q.reminder.reminder.exception.FeishuException;
import com.q.reminder.reminder.vo.*;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.*;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    private final static Date dueDate = DateTime.now().plusDays(7).toDate();
    public final static String REDMINE_URL = "http://redmine-qa.mxnavi.com";
    public final static String REDMINE_PA_URL = "http://redmine-pa.mxnavi.com";


    /**
     * 检查任务是否创建
     *
     * @param transport
     * @param params
     * @return
     */
    public static boolean checkIssue(Transport transport, List<RequestParam> params) {
        List<Issue> issueList;
        try {
            issueList = transport.getObjectsList(Issue.class, params);
        } catch (RedmineException e) {
            log.error("Redmine-[检查是否有redmine任务] 异常 ", e);
            return false;
        }
        return !CollectionUtils.isEmpty(issueList);
    }

    /**
     * 通过项目读取redmine过期任务,只包含打开的状态
     *
     * @param projectInfos
     * @return 按指派人员返回问题列表
     */
    public static List<RedmineVo> queryUserByExpiredDayList(QueryVo vo, List<RProjectInfo> projectInfos) {
        List<String> noneStatusList = vo.getNoneStatusList();
        Integer expiredDay = vo.getExpiredDay();
        List<RedmineVo> allIssueList = new ArrayList<>();
        projectInfos.forEach(projectInfo -> {
            String redmineUrl = projectInfo.getRedmineUrl();
            RedmineManager mgr = RedmineManagerFactory.createWithApiKey(redmineUrl, projectInfo.getPmKey());
            IssueManager issueManager = mgr.getIssueManager();
            try {
                issueManager.getIssues(projectInfo.getPkey(), null).stream().filter(e -> {
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
     * @param projectList
     * @return
     */
    public static List<RedmineVo> queryUpdateIssue(List<RProjectInfo> projectList) {
        List<RedmineVo> issues = new ArrayList<>();
        for (RProjectInfo project : projectList) {
            Transport transport = getTransportByProject(project);
            List<RequestParam> params = List.of(
                    new RequestParam("status_id", "*"),
                    new RequestParam("updated_on", "t"));
            try {
                List<Issue> issueList = transport.getObjectsList(Issue.class, params);
                if (CollectionUtils.isEmpty(issueList)) {
                    continue;
                }
                issueList.forEach(e -> {
                    String assigneeName = e.getAssigneeName();
                    RedmineVo queryRedmineVo = new RedmineVo();
                    queryRedmineVo.setUpdatedOn(e.getUpdatedOn());
                    queryRedmineVo.setSubject(e.getSubject());
                    queryRedmineVo.setRedmineId(String.valueOf(e.getId()));
                    queryRedmineVo.setAuthorName(e.getAuthorName());
                    if (StringUtils.isNotBlank(assigneeName)) {
                        queryRedmineVo.setAssigneeName(assigneeName.replace(" ", ""));
                    }
                    queryRedmineVo.setRedmineUrl(project.getRedmineUrl());
                    issues.add(queryRedmineVo);
                });
            } catch (RedmineException e) {
                String msg = RedmineApi.class.getName() + " projectName " + project.getPname();
                throw new FeishuException(e, msg);
            }
        }
        return issues;
    }

    /**
     * 查询项目耗时
     *
     * @param info
     * @return
     * @throws RedmineException
     */
    public static List<TimeEntry> queryTimes(RProjectInfo info) throws RedmineException {
        String redmineUrl = info.getRedmineUrl();
        Transport transport = getTransportByProject(info);
        Collection<RequestParam> params = new ArrayList<>();
        Date startDay = info.getStartDay();
        if (startDay != null) {
            params.add(new RequestParam("f[]", "spent_on"));
            params.add(new RequestParam("op[spent_on]", ">="));
            params.add(new RequestParam("v[spent_on][]", new DateTime(startDay).toString("yyyy-MM-dd")));
        }
        List<TimeEntry> timeEntries = transport.getObjectsList(TimeEntry.class, params);
        IssueManager issueManager = RedmineManagerFactory.createWithApiKey(redmineUrl, info.getPmKey()).getIssueManager();
        for (TimeEntry timeEntry : timeEntries) {
            Integer issueId = timeEntry.getIssueId();
            Issue issue = issueManager.getIssueById(issueId);
            CustomField customField = issue.getCustomFieldById(CustomFieldsEnum.BUG_TYPE.getId());
            if (customField != null && "Bug".equals(customField.getValue())) {
                timeEntry.addCustomFields(List.of(customField));
            }
        }
        return timeEntries;
    }

    /**
     * 查询项目耗时
     *
     * @param info
     * @return
     * @throws RedmineException
     */
    public static List<TimeEntry> queryProjectUsers(RProjectInfo info) throws RedmineException {
        Transport transport = getTransportByProject(info);
        List<RequestParam> requestParams = List.of(
                new RequestParam("f[]", "spent_on"),
                new RequestParam("op[spent_on]", ">t-"),
                new RequestParam("v[spent_on][]", "5")

        );
        return transport.getObjectsList(TimeEntry.class, requestParams);
    }

    public static Collection<? extends Issue> queryIssues(RProjectInfo info) throws RedmineException {
        Transport transport = getTransportByProject(info);
        Collection<RequestParam> params = new ArrayList<>();
        Date startDay = info.getStartDay();
        if (startDay != null) {
            params.add(new RequestParam("f[]", "updated_on"));
            params.add(new RequestParam("op[updated_on]", ">="));
            params.add(new RequestParam("v[updated_on][]", new DateTime(startDay).toString("yyyy-MM-dd")));
        }
        return transport.getObjectsList(Issue.class, params);
    }

    /**
     * 创建任务并返回任务详情
     *
     * @param issue
     * @param transport
     * @return
     */
    public static Issue createIssue(Issue issue, @NonNull Transport transport) throws RedmineException {
        issue.setStatusId(1).setCreatedOn(new Date());
        issue.setTransport(transport);
        return issue.create();
    }

    public static Transport getTransportByProject(RProjectInfo projectInfo) {
        String url = projectInfo.getRedmineUrl() + "/projects/" + projectInfo.getPkey();
        return RedmineManagerFactory.createWithApiKey(url, projectInfo.getPmKey()).getTransport();
    }

    public static String createSubject(RedmineDataVo featureTmp) {
        StringBuilder subject = new StringBuilder();
        String mdl = featureTmp.getMdl();
        String menuOne = featureTmp.getMenuOne();
        String menuTwo = featureTmp.getMenuTwo();
        String menuThree = featureTmp.getMenuThree();
        if (StringUtils.isNotBlank(mdl)) {
            subject.append("模块：").append(mdl).append("-");
        }
        if (StringUtils.isNotBlank(menuOne)) {
            subject.append("一级：").append(menuOne).append("-");
        }
        if (StringUtils.isNotBlank(menuTwo)) {
            subject.append("二级：").append(menuTwo).append("-");
        }
        if (StringUtils.isNotBlank(menuThree)) {
            subject.append("三级：").append(menuThree);
        }
        int lastChar = subject.lastIndexOf("-");
        if (lastChar == subject.length() - 1 && subject.length() > 0) {
            subject.deleteCharAt(lastChar);
        }
        return subject.toString();
    }

    /**
     * 创建子任务
     *
     * @param transport
     * @param customFields
     * @param ftrType
     * @param records
     * @param testTracker
     * @param devTracker
     */
    public static boolean createSubIssue(Issue parentIssue, Transport transport, List<CustomField> customFields, boolean ftrType, List<FeautreTimeVo> records, Tracker testTracker, Tracker devTracker) {
        boolean createSubIssue = true;
        Integer issueId = parentIssue.getId();
        Issue issue = new Issue();
        if (!ftrType) {
            issue.setParentId(issueId);
        }
        issue.addCustomFields(customFields);
        issue.setDescription(parentIssue.getDescription());
        issue.setProjectId(parentIssue.getProjectId());
        String subject = parentIssue.getSubject();
        issue.setTransport(transport);
        issue.setDueDate(dueDate);
        issue.setStatusId(1);
        issue.setPriorityId(4);

        for (FeautreTimeVo e : records) {
            String name = e.getName();
            Float times = e.getTimes();
            Integer id = e.getId();
            Issue newIssue = issue;
            newIssue.setSpentHours(times);
            newIssue.setAssigneeId(id);
            if ("test".equals(name)) {
                newIssue.setSubject(subject + "-测试用例");
                newIssue.setTracker(testTracker);
                try {
                    createSubIssue = createSubIssue && newIssue.create().getId() != null;
                    newIssue.setSubject(subject + "-测试执行");
                    createSubIssue = createSubIssue && newIssue.create().getId() != null;
                } catch (RedmineException ex) {
                    createSubIssue = false;
                    log.error(ex);
                }
            } else {
                newIssue.setSubject(subject + "-" + ROLE_MAP.get(name));
                newIssue.setTracker(devTracker);
                try {
                    Issue issu = newIssue.create();
                    createSubIssue = createSubIssue && issu.getId() != null;
                } catch (Exception ex) {
                    log.error(ex);
                    createSubIssue = false;
                }
            }
        }
        return createSubIssue;
    }

    private static final Map<String, String> ROLE_MAP = Map.of(
            "test", "测试",
            "front", "前端",
            "back", "后端",
            "bgdt", "大数据",
            "prdct", "产品",
            "andrd", "安卓",
            "algrthm", "算法",
            "oprton", "运维",
            "archtct", "架构",
            "implmntton", "实施"
    );

    /**
     * 查询BUG对应的issue
     * @param projectInfo
     * @param bugParams
     * @return
     * @throws RedmineException
     */
    public static Collection<? extends Issue> queryIssueByBug(RProjectInfo projectInfo, List<RequestParam> bugParams) throws RedmineException {
        Transport transport = getTransportByProject(projectInfo);
        return transport.getObjectsList(Issue.class, bugParams);
    }

    public static Collection<? extends TimeEntry> getTimeEntity(RProjectInfo projectInfo, List<RequestParam> requestParams) throws RedmineException {
        Transport transport = getTransportByProject(projectInfo);
        return transport.getObjectsList(TimeEntry.class, requestParams);
    }

    public static Project queryProjectByKey(RProjectReaVo info) throws RedmineException {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(info.getRedmineUrl(), info.getPmKey());
        return mgr.getProjectManager().getProjectByKey(info.getPkey());
    }

    public static Map<String, String> copq(List<RProjectInfo> list) throws RedmineException {
        Map<String, String> map = new HashMap<>();
        for (RProjectInfo projectInfo : list) {
            // 查询所有工时
            Collection<? extends TimeEntry> timeEntries = getTimeEntity(projectInfo, List.of());
            double sum = timeEntries.stream().mapToDouble(TimeEntry::getHours).sum();
            if (sum == 0) {
                sum = 1;
            }
            // 查询BUG工时
            Collection<? extends TimeEntry> bugTimeEntries = getTimeEntity(projectInfo, List.of(
                    new RequestParam("issue.tracker_id", "6")
            ));
            double bugSum = bugTimeEntries.stream().mapToDouble(TimeEntry::getHours).sum();
            map.put(projectInfo.getPid(), BigDecimal.valueOf(bugSum / sum * 100).setScale(2, RoundingMode.HALF_UP) + "%");
        }
        return map;
    }
}
