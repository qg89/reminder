package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import com.q.reminder.reminder.enums.CustomFieldsEnum;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.internal.RequestParam;
import com.taskadapter.redmineapi.internal.Transport;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.WeeklyProjectReport
 * @Description :
 * @date :  2022.10.31 11:24
 */
public abstract class WeeklyProjectRedmineUtils {

    /**
     * 项目周报，获取评审问题
     */
    public static List<Issue> reviewQuestion(WeeklyProjectVo projectInfo) {
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
    public static List<Issue> externalBugs(WeeklyProjectVo projectInfo) {
        List<RequestParam> params = List.of(
                new RequestParam("f[]", "cf_68"),
                new RequestParam("op[cf_68]", "="),
                new RequestParam("v[cf_68][]", "客户反馈")
        );
        return queryRedmine(projectInfo, params).stream().filter(e -> "Bug".equals(e.getCustomFieldById(CustomFieldsEnum.BUG_TYPE.getId()).getValue())).toList();
    }

//    public static void majorProjectTrackingItems(List<ProjectInfo> projectInfoList, QueryVo vo) {
//        for (ProjectInfo projectInfo : projectInfoList) {
//            List<RequestParam> params = List.of(
//                    // 问题
////                    new RequestParam("category_id", "2798"),
////                    // 风险
////                    new RequestParam("category_id", "2797"),
////                    // 依赖
////                    new RequestParam("category_id", "2799")
//                    new RequestParam("f[]", "category_id"),
//                    new RequestParam("op[category_id]", "="),
//                    new RequestParam("v[category_id][]", "2797"),
//                    new RequestParam("v[category_id][]", "2798"),
//                    new RequestParam("v[category_id][]", "2799")
//            );
//            List<Issue> issues = queryRedmine(projectInfo, params);
//            System.out.println(issues);
//        }
//    }

    public static List<TimeEntry> wProjectTimes(WeeklyProjectVo projectInfo) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(RedmineApi.getRedmineUrl(projectInfo.getRedmineType()) + "/projects/" + projectInfo.getPKey(), projectInfo.getPmKey());
        Transport transport = mgr.getTransport();
        try {
            return transport.getObjectsList(TimeEntry.class, List.of(
                    new RequestParam("f[]", "spent_on"),
                    new RequestParam("op[spent_on]", "<="),
                    new RequestParam("v[spent_on][]", getWeekNumToSunday(projectInfo.getWeekNum()))
            ));
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<TimeEntry> wprojectTimesBugs(WeeklyProjectVo projectInfo) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(RedmineApi.getRedmineUrl(projectInfo.getRedmineType()) + "/projects/" + projectInfo.getPKey(), projectInfo.getPmKey());
        Transport transport = mgr.getTransport();
        List<RequestParam> params = List.of(
                new RequestParam("f[]", "issue.cf_72"),
                new RequestParam("op[issue.cf_72]", "*")
        );
        try {
            return transport.getObjectsList(TimeEntry.class, params);
        } catch (RedmineException e) {
            e.printStackTrace();
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
    private static List<Issue> queryRedmine(WeeklyProjectVo projectInfo, List<RequestParam> params) {
        List<Issue> objectsList = null;
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(RedmineApi.getRedmineUrl(projectInfo.getRedmineType()) + "/projects/" + projectInfo.getPKey(), projectInfo.getPmKey());
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
        public static List<Issue> allBug(WeeklyProjectVo projectInfo) {
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

    private static String getWeekNumToSunday(int weekNum) {
        WeekFields weekFields = WeekFields.ISO;
        //输入你想要的年份和周数
        LocalDate localDate = LocalDate.now().withYear(DateUtil.thisYear()).with(weekFields.weekOfYear(), weekNum);
        return localDate.with(weekFields.dayOfWeek(), 7L).atStartOfDay().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
