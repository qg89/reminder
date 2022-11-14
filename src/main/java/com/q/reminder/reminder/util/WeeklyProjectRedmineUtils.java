package com.q.reminder.reminder.util;

import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.enums.CustomFieldsEnum;
import com.q.reminder.reminder.vo.QueryVo;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.WeeklyProjectReport
 * @Description :
 * @date :  2022.10.31 11:24
 */
public abstract class WeeklyProjectRedmineUtils {


    public static void main(String[] args) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setRedmineUrl("http://redmine-qa.mxnavi.com/");
        projectInfo.setPKey("dcsp-2");
        projectInfo.setPmKey("1f905383da4f783bad92e22f430c7db0b15ae258");
//        List<TimeEntry> copq = wprojectTimesBugs(projectInfo, "2022-11-04");
        List<Issue> issues = OverallBug.allBug(projectInfo);
        System.out.println(issues.get(0).getCustomFields());
        System.out.println();
    }

    /**
     * 项目周报，获取评审问题
     */
    public static List<Issue> reviewQuestion(ProjectInfo projectInfo) {
        List<RequestParam> params = List.of(
                new RequestParam("f[]", "tracker_id"),
                new RequestParam("op[tracker_id]", "="),
                new RequestParam("v[tracker_id][]", "38")
        );
        return queryRedmine(projectInfo, params);
    }

    /**
     * 外部Bug情况
     */
    public static List<Issue> externalBugs(ProjectInfo projectInfo) {
        List<RequestParam> params = List.of(
                new RequestParam("f[]", "cf_68"),
                new RequestParam("op[cf_68]", "="),
                new RequestParam("v[cf_68][]", "客户反馈")
        );
        return queryRedmine(projectInfo, params).stream().filter(e -> "Bug".equals(e.getCustomFieldById(CustomFieldsEnum.BUG_TYPE.getId()).getValue())).toList();
    }

    public static void majorProjectTrackingItems(List<ProjectInfo> projectInfoList, QueryVo vo) {
        for (ProjectInfo projectInfo : projectInfoList) {
            List<RequestParam> params = List.of(
                    // 问题
//                    new RequestParam("category_id", "2798"),
//                    // 风险
//                    new RequestParam("category_id", "2797"),
//                    // 依赖
//                    new RequestParam("category_id", "2799")
                    new RequestParam("f[]", "category_id"),
                    new RequestParam("op[category_id]", "="),
                    new RequestParam("v[category_id][]", "2797"),
                    new RequestParam("v[category_id][]", "2798"),
                    new RequestParam("v[category_id][]", "2799")
            );
            List<Issue> issues = queryRedmine(projectInfo, params);
            System.out.println(issues);
        }
    }

    public static List<TimeEntry> wprojectTimes(ProjectInfo projectInfo) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(projectInfo.getRedmineUrl() + "/projects/" + projectInfo.getPKey(), projectInfo.getPmKey());
        Transport transport = mgr.getTransport();
        List<RequestParam> params = new ArrayList<>();
        try {
            return transport.getObjectsList(TimeEntry.class, params);
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<TimeEntry> wprojectTimesBugs(ProjectInfo projectInfo, String custType) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(projectInfo.getRedmineUrl() + "/projects/" + projectInfo.getPKey(), projectInfo.getPmKey());
        Transport transport = mgr.getTransport();
        List<RequestParam> params = List.of(
                new RequestParam("f[]", "issue.cf_72"),
                new RequestParam("op[issue.cf_72]", "="),
                new RequestParam("v[issue.cf_72][]", custType)
        );
        try {
//            List<TimeEntry> timeEntryList = transport.getObjectsList(TimeEntry.class, params);
//            double allHourse = timeEntryList.stream().collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();
//            double sum = timeEntryList.stream().filter(e -> new DateTime("2022-08-01").before(e.getCreatedOn())).collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();

            return transport.getObjectsList(TimeEntry.class, params);
        } catch (RedmineException e) {

        }
        return null;
    }

    /**
     * 查询redmine
     *
     * @param projectInfo
     * @param params
     * @return
     */
    private static List<Issue> queryRedmine(ProjectInfo projectInfo, List<RequestParam> params) {
        List<Issue> objectsList = null;
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(projectInfo.getRedmineUrl() + "/projects/" + projectInfo.getPKey(), projectInfo.getPmKey());
        Transport transport = mgr.getTransport();
        try {
            objectsList = transport.getObjectsList(Issue.class, params);
        } catch (RedmineException e) {

        }
        return objectsList;
    }

    /**
     * 整体BUG（上周）
     */
    public static class OverallBug {
        /**
         * 全量BUG
         * @param projectInfo
         * @return
         */
        public static List<Issue> allBug(ProjectInfo projectInfo) {
            List<RequestParam> params = List.of(
                    new RequestParam("f[]", "tracker_id"),
                    new RequestParam("op[tracker_id]", "="),
                    new RequestParam("v[tracker_id][]", "1")
            );
            List<String> bugType = List.of("Bug", "长期改善");
            // BUG分类
            return queryRedmine(projectInfo, params).stream().filter(e -> bugType.contains(e.getCustomFieldById(CustomFieldsEnum.BUG_TYPE.getId()).getValue())).toList();
        }
    }
}
