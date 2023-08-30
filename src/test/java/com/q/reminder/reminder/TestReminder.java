package com.q.reminder.reminder;

import cn.hutool.core.date.DateUtil;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.CustomField;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.Version;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.test.TestReminder
 * @Description :
 * @date :  2023.03.24 10:46
 */
public class TestReminder {

    static Transport transport = RedmineManagerFactory.createWithApiKey("http://redmine-pa.mxnavi.com", "e47f8dbff40521057e2cd7d6d0fed2765d474d4f").getTransport();

    public static void main(String[] args) throws RedmineException {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey("http://redmine-pa.mxnavi.com", "e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
        RProjectInfo in = new RProjectInfo();
        in.setPmKey("e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
        in.setRedmineType("2");
        List<RequestParam> requestParams = List.of(
                new RequestParam("f[]", "spent_on"),
                new RequestParam("op[spent_on]", ">t-"),
                new RequestParam("v[spent_on][]", "4")
        );
//        Collection<? extends TimeEntry> spentOn = RedmineApi.listAllTimes(in, List.of(
//                new RequestParam("f[]", "spent_on"),
//                new RequestParam("op[spent_on]", ">t-"),
//                new RequestParam("v[spent_on][]", "5")
//        ));

//        Collection<? extends TimeEntry> bugTimeEntries = RedmineApi.listAllTimes(in, List.of(
//                new RequestParam("issue.tracker_id.0", "4"),
//                new RequestParam("issue.tracker_id.1", "6"),
//                new RequestParam("project_id.2", "260")
//        ));

//        List<TimeEntry> list = RedmineApi.listAllTimes(in, requestParams);
//        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();
//        Map<String, String> map = new HashMap<>x();
//        map.put("spend_on", "2023-03-31");
//        ResultsWrapper<TimeEntry> timeEntries = timeEntryManager.getTimeEntries(map);


        List<TimeEntry> objectsList = mgr.getTransport().getObjectsList(TimeEntry.class, requestParams);
        System.out.println(objectsList);

////        Integer cp101003 = mgr.getProjectManager().getProjectByKey("510303-gb").getId();
////        System.out.println(cp101003);
//        ProjectInfo info = new ProjectInfo();
//        info.setVersion(2329);
//        info.setPid(249);
////        issueList(mgr, info);
////        List<Integer> status = List.of(2, 3, 6, 4);
//        List<String> overId = List.of("1", "2", "3", "4", "5", "6", "8", "9", "10");
////        dev(List.of(issueById), status, info);
//        info.setOverList(overId);
//        List<RProjectInfo> projectInfoList = new ArrayList<>();
//        projectInfoList.add(in);
////        List<RedmineVo> redmineVos = RedmineApi.queryUpdateIssue(projectInfoList);
//
//        List<RequestParam> requestParams = List.of(
//                new RequestParam("f[]", "created_on"),
//                new RequestParam("op[created_on]", "lm")
//        );
//        Collection<? extends Issue> issues = RedmineApi.queryIssueByBug(in, requestParams);


//        updateStatus(mgr, info);
//        DateTime time = DateTime.now();
//        String end = "2023-07-31";
//        boolean flag = true;
//        int day = 0;
//        while (flag) {
//            String now = time.toString("yyyy-MM-dd");
//            if (now.equals(end)) {
//                flag = false;
//            }
//            time = time.plusDays(1);
//            Holiday holiday = HolidayUtil.getHoliday(now);
//            System.out.println(holiday);
//            if ((holiday == null && !DateUtil.isWeekend(time.toDate())) || holiday != null && holiday.isWork()) {
//                day++;
//            }
//        }
//        System.out.println(day);
//
//        RProjectInfo projectInfo = new RProjectInfo();
//        projectInfo.setPkey("cp422001-_im");
//        projectInfo.setPmKey("e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
//        projectInfo.setRedmineUrl("https://redmine-pa.mxnavi.com");
//        List<RedmineVo> redmineVos = RedmineApi.queryUpdateIssue(List.of(projectInfo));
//        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();
//        List<TimeEntryActivity> timeEntryActivities = timeEntryManager.getTimeEntryActivities();
    }

    private static void issueList(RedmineManager mgr, ProjectInfo info) throws RedmineException {
        List<RequestParam> params = List.of(new RequestParam("f[]", "status_id"), new RequestParam("op[status_id]", "*"));

        List<Issue> issueList = mgr.getTransport().getObjectsList(Issue.class, params);
        issueList.stream().filter(e -> !DateUtil.format(e.getCreatedOn(), "yyyy-MM-dd").equals(DateUtil.today())).collect(Collectors.groupingBy(e -> String.valueOf(e.getTracker().getId()))).forEach((tracker, list) -> {
            switch (tracker) {
//                case "1" -> {
//                    // Bug-研发
//                    List<Integer> status = List.of(2, 4);
//                    dev(list, status, info, false);
//                }
//                case "2" -> {
//                    // 需求:新——>处理中->已解决->转测试->转产品->已关闭
//                    List<Integer> status = List.of(2, 3, 6, 7, 4);
//                    dev(list, status, info, false);
//                }
//                case "3" -> {
//                    // 研发
//                    List<Integer> status = List.of(2, 3, 4);
//                    dev(list, status, info, false);
//                }
//                case "4" -> {
//                    // 测试
//                    List<Integer> status = List.of(2, 3, 4);
//                    dev(list, status, info, false);
//                }
//                case "5" -> {
//                    // 技术课题
//                    List<Integer> status = List.of(2, 3, 4);
//                    dev(list, status, info, false);
//                }
                case "6" -> {
                    // Bug-ST
                    List<Integer> status = List.of(2, 3, 6, 4);
                    dev(list, status, info, false);
                }
                case "8" -> {
                    // 评审缺陷
                    List<Integer> status = List.of(2, 3, 4);
                    dev(list, status, info, false);
                }
//                case "9" -> {
//                    // 管理
//                    System.out.println(list);
//                }
//                case "10" -> {
//                    // 产品定义
//                    System.out.println(list);
//                }
                default -> {
                }
            }

        });
    }


    public static void bug(List<Issue> list, ProjectInfo info) {
        Integer pid = info.getPid();
        list.forEach(issue -> {
            issue.setProjectId(pid);
            try {
                Issue issueNew = issue.create();
                for (int i = 2; i < 5; i++) {
                    issueNew.setStatusId(i);
                    issueNew.update();
                }
            } catch (RedmineException e) {
                e.printStackTrace();
            }
        });
    }

    public static void dev(List<Issue> list, List<Integer> status, ProjectInfo info, boolean update) {
        Integer version = info.getVersion();
        Integer pid = info.getPid();
        list.forEach(issue -> {
            issue.setTransport(transport);
            issue.setProjectId(pid);
            Collection<CustomField> customFields = issue.getCustomFields();
            customFields.forEach(e -> {
                Integer customId = e.getId();
                String value = e.getValue();
                if (e.getName().contains("版本")) {
                    e.setValue(String.valueOf(version));
                }
                if (customId == 237 && StringUtils.isBlank(value)) {
                    e.setValue("-");
                }
                if (customId == 239 && StringUtils.isBlank(value)) {
                    e.setValue("编码");
                }
                if (customId == 219 && StringUtils.isBlank(value)) {
                    e.setValue("1244");
                }
                if (customId == 221 && StringUtils.isBlank(value)) {
                    e.setValue("已投入/已解决");
                }
                if (customId == 218 && StringUtils.isBlank(value)) {
                    e.setValue("1244");
                }
                if (customId == 236 && StringUtils.isBlank(value)) {
                    e.setValue("-");
                }
                if (customId == 220 && StringUtils.isBlank(value)) {
                    e.setValue("1341");
                }
                if (customId == 229 && StringUtils.isBlank(value)) {
                    e.setValue("1/1");
                }
                if (customId == 231 && StringUtils.isBlank(value)) {
                    e.setValue("D");
                }
                if (customId == 232 && StringUtils.isBlank(value)) {
                    e.setValue("测试组");
                }
                if (customId == 233 && StringUtils.isBlank(value)) {
                    e.setValue("机上用例测试");
                }
            });
            Map<String, String> customMap = customFields.stream().collect(Collectors.toMap(e -> String.valueOf(e.getId()), v -> {
                if (StringUtils.isBlank(v.getValue())) {
                    return "";
                } else {
                    return v.getValue();
                }
            }, (v1, v2) -> v1));
            try {
                Version targetVersion = Optional.ofNullable(issue.getTargetVersion()).orElse(new Version());
                targetVersion.setId(version);
                targetVersion.setProjectId(pid);
                targetVersion.setTransport(transport);
                issue.setTargetVersion(targetVersion);
                Issue issueNew;
                if (update) {
                    issueNew = issue;
                } else {
                    issueNew = issue.create();
                }
                issueNew.setTargetVersion(targetVersion);
                for (Integer i : status) {
                    issueNew.getCustomFields().forEach(e -> {
                        Integer versionId = e.getId();
                        if (versionId == 240 || versionId == 230) {
                            e.setValue(version.toString());
                        } else {
                            e.setValue(customMap.get(versionId.toString()));
                        }
                    });
                    issueNew.setStatusId(i);
                    issueNew.update();
                }
            } catch (RedmineException e) {
                e.printStackTrace();
            }
        });
    }

    public static void updateStatus(RedmineManager mgr, ProjectInfo info) {
        IssueManager issueManager = mgr.getIssueManager();
        info.getOverList().forEach(id -> {
            List<Issue> results;
            try {
                results = issueManager.getIssues(Map.of("tracker_id", id)).getResults();
            } catch (RedmineException e) {
                throw new RuntimeException(e);
            }
            if (CollectionUtils.isEmpty(results)) {
                return;
            }
            List<Integer> status = new ArrayList<>();
            switch (id) {
                case "1" -> {
                    // Bug-研发
                    status = List.of(2, 4);
                }
                case "2" -> {
                    // 需求:新——>处理中->已解决->转测试->转产品->已关闭
                    status = List.of(2, 3, 6, 7, 4);
                }
                case "3" -> {
                    // 研发
                    status = List.of(2, 3, 4);
                }
                case "4" -> {
                    // 测试
                    status = List.of(2, 3, 4);
                }
                case "5" -> {
                    // 技术课题
                    status = List.of(2, 3, 4);
                }
                case "6" -> {
                    // Bug-ST
                    status = List.of(2, 3, 6, 4);
                }
                case "8" -> {
                    // 评审缺陷
                    status = List.of(2, 3, 4);
                }
                case "9" -> {
                    // 管理
                }
                case "10" -> {
                    // 产品定义
                }
                default -> {
                }
            }
            dev(results, status, info, true);
        });
    }

    static class ProjectInfo {
        private Integer version;
        private Integer pid;
        private String url;
        private String key;
        private String defaultTest;
        private String defaultProduct;
        private List<String> overList;

        public Integer getVersion() {
            return version;
        }

        public void setVersion(Integer version) {
            this.version = version;
        }

        public Integer getPid() {
            return pid;
        }

        public void setPid(Integer pid) {
            this.pid = pid;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getDefaultTest() {
            return defaultTest;
        }

        public void setDefaultTest(String defaultTest) {
            this.defaultTest = defaultTest;
        }

        public String getDefaultProduct() {
            return defaultProduct;
        }

        public void setDefaultProduct(String defaultProduct) {
            this.defaultProduct = defaultProduct;
        }

        public List<String> getOverList() {
            return overList;
        }

        public void setOverList(List<String> overList) {
            this.overList = overList;
        }
    }
}
