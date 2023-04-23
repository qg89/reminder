package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.enums.CustomFieldsEnum;
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
import org.joda.time.format.DateTimeFormat;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static Date dueDate = DateTime.now().plusDays(7).toDate();

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
     * @param RProjectInfoList
     * @return
     */
    public static List<RedmineVo> queryUpdateIssue(List<RProjectInfo> RProjectInfoList) {
        List<RedmineVo> issues = new ArrayList<>();
        for (RProjectInfo project : RProjectInfoList) {
            RedmineManager mgr = RedmineManagerFactory.createWithApiKey(project.getRedmineUrl(), project.getPmKey());
            Transport transport = mgr.getTransport();
            List<RequestParam> params = List.of(
                    new RequestParam("project_id", project.getPid()),
                    new RequestParam("status_id", "*"),
//                    new RequestParam("assigned_to_id", "*"),
                    new RequestParam("updated_on", DateUtil.today()));
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
                log.error("Redmine-查询当天更新的任务异常");
            }
        }
        return issues;
    }

    /**
     * 获取指派给为空的任务
     * @param projectInfos
     * @return
     */
    public static List<RedmineVo> queryUpdateIssueNameNone(List<RProjectInfo> projectInfos) {
        List<RedmineVo> issues = new ArrayList<>();
        for (RProjectInfo project : projectInfos) {
            RedmineManager mgr = RedmineManagerFactory.createWithApiKey(project.getRedmineUrl(), project.getPmKey());
            Transport transport = mgr.getTransport();
            List<RequestParam> params = List.of(
                    new RequestParam("project_id", project.getPid()),
                    new RequestParam("status_id", "*"),
                    new RequestParam("assigned_to_id", "!*")
//                    new RequestParam("due_date", "!*")
            );
            try {
                transport.getObjectsList(Issue.class, params).forEach(e -> {
                    String assigneeName = e.getAssigneeName();
                    RedmineVo queryRedmineVo = new RedmineVo();
                    queryRedmineVo.setUpdatedOn(e.getUpdatedOn());
                    queryRedmineVo.setSubject(e.getSubject());
                    queryRedmineVo.setRedmineId(e.getId() + "");
                    queryRedmineVo.setAuthorName(e.getAuthorName());
                    queryRedmineVo.setProjectName(project.getPname());
                    queryRedmineVo.setStatusName(e.getStatusName());
                    queryRedmineVo.setDueDate(e.getDueDate());
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
        String redmineType = definition.getRedmineType();
        Integer productAssigneeId = redmineUserMap.get(convertAssid(definition.getProduct(), redmineType));
        Integer bigDataAssigneeId = redmineUserMap.get(convertAssid(definition.getBigData(), redmineType));
        Integer backendAssigneeId = redmineUserMap.get(convertAssid(definition.getBackend(), redmineType));
        Integer testAssigneeId = redmineUserMap.get(convertAssid(definition.getTest(), redmineType));
        Integer algorithmAssigneeId = redmineUserMap.get(convertAssid(definition.getAlgorithm(), redmineType));
        Integer frontAssigneeId = redmineUserMap.get(convertAssid(definition.getFront(), redmineType));
        Integer projectId = definition.getProjectId();

        for (FeatureListVo vo : featureList) {
            String devT = vo.getDevTime();
            String prodT = vo.getProdTime();
            Date devTime = DateTime.now().plusDays(10).toDate();
            Date prodTime = devTime;
            Date today = DateTime.parse(DateUtil.today()).toDate();
            if (StringUtils.isNotBlank(devT) && isValidDate(devT, "yyyyMMdd")) {
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
            String menuOne = vo.getMenuOne().replace("[", "【").replace("]", "】");
            String menuTwo = vo.getMenuTwo().replace("[", "【").replace("]", "】");
            String menuThree = vo.getMenuThree().replace("[", "【").replace("]", "】");
            String front = vo.getFront();
            String backend = vo.getBackend();
            String bigData = vo.getBigData();
            String algorithm = vo.getAlgorithm();
            String test = vo.getTest();
            String desc = vo.getDesc();
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

            boolean check = checkRedmineTask(transport, redmineSubject);
            if (check) {
                return;
            }

            List<CustomField> customFieldList = new ArrayList<>();
            CustomField customField = new CustomField().setName(CustomFieldsEnum.FEATURE_ID.getName()).setId(CustomFieldsEnum.FEATURE_ID.getId()).setValue(featureId);
            customFieldList.add(customField);
            customField = new CustomField().setId(CustomFieldsEnum.FEATURE_TYPE.getId()).setName(CustomFieldsEnum.FEATURE_TYPE.getName()).setValue("功能");
            customFieldList.add(customField);
            customField = new CustomField().setId(CustomFieldsEnum.REQUIRE_VALIDATION.getId()).setName(CustomFieldsEnum.REQUIRE_VALIDATION.getName()).setValue("是");
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
                continue;
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
//            if (StringUtils.isNotBlank(parentFeatureId) && StringUtils.isNotBlank(featureType) && "变更".equals(featureType)) {
//                Integer parentId = getParentId(transport, parentFeatureId);
//                createParentFeatureId(parentId, newIssueId, definition);
//            }
        }
    }

    private static String convertAssid(String name, String redmineType) {
        String chars = "-" + redmineType;
        if (name.contains(chars)) {
            name = name.replace(chars, "");
        }
        return name;
    }


    /**
     * 检查是否有redmine任务
     *
     * @param transport
     * @param redmineSubject
     * @return
     */
    private static boolean checkRedmineTask(Transport transport, String redmineSubject) {
        List<RequestParam> params = List.of(new RequestParam("f[]", "subject"),
                new RequestParam("op[subject]", "~"),
                new RequestParam("v[subject][]", redmineSubject));
        List<Issue> issueList;
        try {
            issueList = transport.getObjectsList(Issue.class, params);
        } catch (RedmineException e) {
            log.error("Redmine-[检查是否有redmine任务] 异常 ", e);
            return false;
        }
        if (CollectionUtils.isEmpty(issueList)) {
            return false;
        }
        return true;
    }

    /**
     * 创建子任务
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

    public static boolean isValidDate(String str, String format) {
        if (format == null) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        boolean convertSuccess = true;
        // 指定日期格式
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            sdf.setLenient(false);
            sdf.parse(str);
            if (str.length() != format.length()) {
                convertSuccess = false;
            }
        } catch (ParseException e) {
            // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            convertSuccess = false;
        }
        return convertSuccess;
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
        String url = redmineUrl + "/projects/" + info.getPkey();
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(url, info.getPmKey());
        Transport transport = mgr.getTransport();
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
    public static List<TimeEntry> queryUserTime(RProjectInfo info) throws RedmineException {
        String redmineUrl = info.getRedmineUrl();
        String pmKey = info.getPmKey();
        String url = redmineUrl + "/projects/" + info.getPkey();
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(url, pmKey);
        Transport transport = mgr.getTransport();
        Collection<RequestParam> params = new ArrayList<>();
        params.add(new RequestParam("f[]", "spent_on"));
        params.add(new RequestParam("op[spent_on]", ">="));
        params.add(new RequestParam("v[spent_on][]", new DateTime().minusDays(7).toString("yyyy-MM-dd")));
        return transport.getObjectsList(TimeEntry.class, params);
    }

    public static Collection<? extends Issue> queryIssues(RProjectInfo info) throws RedmineException {
        String redmineUrl = info.getRedmineUrl();
        String url = redmineUrl + "/projects/" + info.getPkey();
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(url, info.getPmKey());
        Transport transport = mgr.getTransport();
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

    public static Transport getTransportByProject(RProjectInfo RProjectInfo) {
        return RedmineManagerFactory.createWithApiKey(RProjectInfo.getRedmineUrl() + "/projects/" + RProjectInfo.getPkey(), RProjectInfo.getPmKey()).getTransport();
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
}
