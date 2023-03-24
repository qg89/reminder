package com.q.reminder.reminder.test;

import com.q.reminder.reminder.entity.RProjectInfo;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.test.TestReminder
 * @Description :
 * @date :  2023.03.24 10:46
 */
public class TestReminder {
    public static void main(String[] args) throws RedmineException {
//        ProjectInfo info = new ProjectInfo();
//        info.setRedmineUrl("http://redmine-pa.mxnavi.com");
//        info.setPKey("510303-gb");
//        info.setPmKey("e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
//
//        List<TimeEntry> timeEntries = queryTimes(info);
//        Map<Integer, String> collect = timeEntries.stream().collect(Collectors.toMap(TimeEntry::getUserId, TimeEntry::getUserName, (v1, v2) -> v1));
////        List<TimeEntry> timeEntries = queryTimes(info).stream().filter(e -> e.getIssueId() == 609043).toList();
////        Map<String, Map<String, Double>> collect = timeEntries.stream().collect(Collectors.groupingBy(e -> String.valueOf(e.getUserId()), Collectors.groupingBy(e -> new DateTime(e.getSpentOn()).toString("yyyy-MM-dd"),
////                Collectors.summingDouble(e -> BigDecimal.valueOf(e.getHours()).setScale(2, RoundingMode.HALF_UP).doubleValue()))));
//        System.out.println(collect);
//        RedmineManager mgr = RedmineManagerFactory.createWithApiKey("http://redmine-qa.mxnavi.com/projects/203301_vehicle_digitization", "f4883d6d5f03ca5ed14cc168ec578c4e7d396c20");
//        Issue issue = new Issue();
//        issue.setSubject("需求ID：[recVGCsjk]-模块：系统管理-一级：用户管理");
//        issue.setDescription("页面功能补充+原型");
//        issue.setAssigneeId(2529);
//        issue.setProjectId(1699);
//        Tracker tracker = new Tracker();
//        tracker.setId(2);
//        issue.setTracker(tracker);
//        issue.setDueDate(new DateTime().plusDays(7).toDate());
//        issue.setStartDate(new Date());
//        issue.setStatusId(1);
//        CustomField recVlGCsjk = RedmineConfig.type("1").setCustomValue("recVlGCsjk");
//        List<CustomField> L = new ArrayList<>(RedmineConfig.CUSTOM_FIELDS);
//        L.add(recVlGCsjk);
//        issue.addCustomFields(L);
//        issue.setTransport(mgr.getTransport());
//        issue.create();
        List<RProjectInfo> l = new ArrayList<>();
        RProjectInfo rProjectInfo = new RProjectInfo();
        rProjectInfo.setRedmineUrl("http://redmine-pa.mxnavi.com/");
        rProjectInfo.setPmKey("e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
        rProjectInfo.setPkey("511304-dcsp-batch2");
        rProjectInfo.setPid("35");
        l.add(rProjectInfo);
        rProjectInfo = new RProjectInfo();
        rProjectInfo.setRedmineUrl("http://redmine-qa.mxnavi.com/");
        rProjectInfo.setPmKey("1f905383da4f783bad92e22f430c7db0b15ae258");
        rProjectInfo.setPkey("511303-dcsp");
        rProjectInfo.setPid("1865");
        l.add(rProjectInfo);
        rProjectInfo.setPkey("dcsp-2");
        rProjectInfo.setPid("1806");
        l.add(rProjectInfo);
        for (RProjectInfo project : l) {
            RedmineManager mgr = RedmineManagerFactory.createWithApiKey(project.getRedmineUrl(), project.getPmKey());
            Transport transport = mgr.getTransport();
            List<RequestParam> params = List.of(
                    new RequestParam("project_id", project.getPid()),
                    new RequestParam("status_id", "*"),
//                    new RequestParam("assigned_to_id", "!*")
                    new RequestParam("due_date", "!*")
            );
            List<Issue> objectsList = transport.getObjectsList(Issue.class, params).stream().filter(e -> !Objects.equals(e.getAssigneeId(), 1244)).toList();
            objectsList.forEach(e -> {
                try {
                    e.setDueDate(e.getClosedOn());
                    e.setTransport(transport);
                    e.update();
                } catch (RedmineException ex) {
                    ex.printStackTrace();
                }
            });

//        List<RequestParam> params = List.of(new RequestParam("f[]", "cf_5"),
//                new RequestParam("op[cf_5]", "~"),
//                new RequestParam("v[cf_5][]", "recVlGCsjk"));
//        List<Issue> issueList = null;
//        try {
//            issueList = mgr.getTransport().getObjectsList(Issue.class, params);
//        } catch (RedmineException e) {
//            log.error("Redmine-[检查是否有redmine任务] 异常 ", e);
//        }
//        System.out.println(issueList);
//        Issue issueById = issueManager.getIssueById(619140);
//        PropertyStorage storage = issueById.getStorage();
//        System.out.println(storage.getProperties());
//        RProjectInfo vo = new RProjectInfo();
//        vo.setRedmineUrl("http://redmine-pa.mxnavi.com");
//        vo.setPmKey("af8ef2224ec0842bc633577d23fe032b9d66025a");
//        vo.setPKey("510303-gb");

//        RedmineManager mgr = RedmineManagerFactory.createWithApiKey("http://redmine-pa.mxnavi.com", "e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
//        IssueManager issueManager = mgr.getIssueManager();
//        Issue issueById = issueManager.getIssueById(4090);
            System.out.println();
//        List<TimeEntry> timeEntries = queryUserTime(vo);
//        System.out.println(timeEntries);
        }
    }
}
