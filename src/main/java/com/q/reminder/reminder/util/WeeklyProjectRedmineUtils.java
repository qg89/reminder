package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateTime;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.vo.QueryVo;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.WeeklyProjectReport
 * @Description :
 * @date :  2022.10.31 11:24
 */
public abstract class WeeklyProjectRedmineUtils {

    /**
     * 评审问题数量
     */
    public static void main(String[] args) {
        ProjectInfo projectInfo = new ProjectInfo();
        projectInfo.setAccessKey("1f905383da4f783bad92e22f430c7db0b15ae258");
        projectInfo.setPKey("dcsp-2");
        projectInfo.setRedmineUrl("http://redmine-qa.mxnavi.com");
        List<ProjectInfo> list = List.of(projectInfo);
        QueryVo vo = new QueryVo();
//        externalBugs(list, vo);
//        reviewQuestion(list, vo);
//        OverallBug.bugLevelDistribution(list, vo);
//        OverallBug.openBugLevelDistribution(list, vo);
//        copq(list, vo);
//        majorProjectTrackingItems(list, vo);
    }

    /**
     * 项目周报，获取评审问题
     *
     */
    public static List<Issue> reviewQuestion(ProjectInfo projectInfo) {
        List<RequestParam> params = List.of(
                new RequestParam("f[]", "tracker_id"),
                new RequestParam("op[tracker_id]", "="),
                new RequestParam("v[tracker_id][]", "38")
        );
        return queryRedmine(projectInfo, new QueryVo(), params);
    }

    /**
     * 外部Bug情况
     */
    public static void externalBugs(List<ProjectInfo> projectInfoList, QueryVo vo) {
        for (ProjectInfo projectInfo : projectInfoList) {
            List<RequestParam> params = List.of(
                    new RequestParam("f[]", "cf_68"),
                    new RequestParam("op[cf_68]", "="),
                    new RequestParam("v[cf_68][]", "客户反馈")
            );
            List<Issue> issues = queryRedmine(projectInfo, vo, params);
            System.out.println(issues);
        }
    }


    /**
     * 整体BUG（上周）
     */
    public static class OverallBug {

        /**
         * ALL Bug等级分布
         */
        public static void bugLevelDistribution(List<ProjectInfo> projectInfoList, QueryVo vo) {
            for (ProjectInfo projectInfo : projectInfoList) {
                List<RequestParam> params = List.of(
                        new RequestParam("tracker_id", "1"),
                        new RequestParam("created_on", "lw"),
                        new RequestParam("status_id", "*")
                );
                List<Issue> issues = queryRedmine(projectInfo, vo, params);
                System.out.println(issues);
            }
        }

        /**
         * open Bug等级分布
         */
        public static void openBugLevelDistribution(List<ProjectInfo> projectInfoList, QueryVo vo) {
            for (ProjectInfo projectInfo : projectInfoList) {
                List<RequestParam> params = List.of(
                        new RequestParam("tracker_id", "1"),
                        new RequestParam("created_on", "lw"),
                        new RequestParam("status_id", "o")
                );
                List<Issue> issues = queryRedmine(projectInfo, vo, params);
                System.out.println(issues);
            }
        }

    }

    public static void copq(List<ProjectInfo> projectInfoList, QueryVo vo) {
        for (ProjectInfo projectInfo : projectInfoList) {
            RedmineManager mgr = RedmineManagerFactory.createWithApiKey(projectInfo.getRedmineUrl() + "/projects/" + projectInfo.getPKey(), projectInfo.getAccessKey());
            Transport transport = mgr.getTransport();
            List<RequestParam> params = List.of();
            try {
                List<TimeEntry> timeEntryList = transport.getObjectsList(TimeEntry.class, params);
                double allHourse = timeEntryList.stream().collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();
                double sum = timeEntryList.stream().filter(e -> new DateTime("2022-08-01").before(e.getCreatedOn())).collect(Collectors.summarizingDouble(TimeEntry::getHours)).getSum();
                System.out.println(timeEntryList);
            } catch (RedmineException e) {

            }
        }
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
            List<Issue> issues = queryRedmine(projectInfo, vo, params);
            System.out.println(issues);
        }
    }

    /**
     * 查询redmine
     * @param projectInfo
     * @param vo
     * @param params
     * @return
     */
    private static List<Issue> queryRedmine(ProjectInfo projectInfo, QueryVo vo, List<RequestParam> params) {
        List<Issue> objectsList = null;
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(projectInfo.getRedmineUrl() + "/projects/" + projectInfo.getPKey(), projectInfo.getAccessKey());
        Transport transport = mgr.getTransport();
        try {
            objectsList = transport.getObjectsList(Issue.class, params);
        } catch (RedmineException e) {

        }
        return objectsList;
    }
}
