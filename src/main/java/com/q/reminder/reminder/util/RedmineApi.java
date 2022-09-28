package com.q.reminder.reminder.util;

import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.Issue;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.RedmineApi
 * @Description :
 * @date :  2022.09.26 15:14
 */
@Log4j2
public class RedmineApi {
    public static final String REDMINE_URL = "http://redmine-qa.mxnavi.com/";

//    public static void query() throws RedmineException {
//        String f = "utf8: ✓\n" +
//                "set_filter: 1\n" +
//                "f[]: status_id\n" +
//                "op[status_id]: =\n" +
//                "v[status_id][]: 1\n" +
//                "f[]: project_id\n" +
//                "op[project_id]: =\n" +
//                "v[project_id][]: 510302-sell\n" +
//                "v[project_id][]: 203301_vehicle_digitization\n" +
//                "v[project_id][]: 203302_4s_eservice\n" +
//                "v[project_id][]: dcsp-2\n" +
//                "f[]: due_date\n" +
//                "op[due_date]: <=\n" +
//                "v[due_date][]: 2022-09-26\n" ;
//        String[] groupF = StringUtils.split(f, "\n");
//        RedmineManager mgr = RedmineManagerFactory.createWithApiKey("http://redmine-qa.mxnavi.com/", "1f905383da4f783bad92e22f430c7db0b15ae258");
//        IssueManager issueManager = mgr.getIssueManager();
//        Params params = new Params();
//        for (String s : groupF) {
//            String[] kv = s.split(":");
//            params.add(StringUtils.trim(kv[0]), StringUtils.trim(kv[1]));
//        }
//        params.add("offset", "0");
//        params.add("limit", "100");
//
//
//        List<Issue> results = issueManager.getIssues(params).getResults();
//        System.out.println(results);
//    }

    public static Map<String, List<Issue>> queryUserList(Set<String> projects, List<String> noneStatusList) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(REDMINE_URL, "1f905383da4f783bad92e22f430c7db0b15ae258");
        IssueManager issueManager = mgr.getIssueManager();
        List<Issue> allIssueList = new ArrayList<>();
        projects.forEach(p -> {
            try {
                List<Issue> issues = issueManager.getIssues(p, null, Include.watchers);
                List<Issue> issueList = issues.stream().filter(e -> {
                    Date dueDate = e.getDueDate();
                    return dueDate != null && new DateTime().minusDays(1).isAfter(new DateTime(dueDate)) && !noneStatusList.contains(e.getStatusName()) && StringUtils.isNotBlank(e.getAssigneeName());
                }).collect(Collectors.toList());
                allIssueList.addAll(issueList);
            } catch (RedmineException e) {
                log.error("读取redmine异常");
            }
        });
        return allIssueList.stream().collect(Collectors.groupingBy(Issue::getAssigneeName));
    }
}
