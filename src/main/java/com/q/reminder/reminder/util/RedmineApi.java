package com.q.reminder.reminder.util;

import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;
import com.q.reminder.reminder.vo.QueryRedmineVo;
import com.taskadapter.redmineapi.*;
import com.taskadapter.redmineapi.bean.Issue;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.bean.Tracker;
import com.taskadapter.redmineapi.internal.ResultsWrapper;
import com.taskadapter.redmineapi.internal.Transport;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;

import java.io.IOException;
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

    private static final String LOGIN_URL = "http://redmine-qa.mxnavi.com/login";
    private static final HashMap<String, List<Cookie>> COOKIE_STORE = new HashMap<>();


    /**
     * 通过项目读取redmine过期任务
     *
     * @param vo
     * @return 按指派人员返回问题列表
     */
    public static Map<String, List<Issue>> queryUserByExpiredDayList(QueryRedmineVo vo) {
        List<String> noneStatusList = vo.getNoneStatusList();
        Integer expiredDay = vo.getExpiredDay();
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(vo.getRedmineUrl(), vo.getApiAccessKey());
        IssueManager issueManager = mgr.getIssueManager();
        List<Issue> allIssueList = new ArrayList<>();
        Set<String> projects = vo.getProjects();
        projects.forEach(p -> {
            try {
                List<Issue> issues = issueManager.getIssues(p, null, Include.watchers);
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
        return allIssueList.stream().collect(Collectors.groupingBy(Issue::getAssigneeName));
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
        IssueManager issueManager = mgr.getIssueManager();
        Params params = new Params();
        params.add("f[]", "subject");
        params.add("op[subject]", "~");
        params.add("v[subject][]", "CID:[" + vo.getCid() + "]");
        ResultsWrapper<Issue> issues = null;
        try {
            issues = issueManager.getIssues(params);
        } catch (RedmineException e) {
            log.error("[保存到redmine任务]异常 ", e);
        }
        if (issues == null) {
            log.info("[保存到redmine任务] 失败,未查询相关任务");
            return;
        }
        List<Issue> results = issues.getResults();
        if (results != null && !results.isEmpty()) {
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
        Transport transport = mgr.getTransport();
        issue.setTransport(transport);
        try {
            issue.create();
        } catch (RedmineException e) {
            log.error("创建redmine任务异常", e);
        }
    }

    /**
     * TODO
     * 查询工时
     *
     * @param apiAccessKey
     * @param redmineUrl
     */
    private static void timeEntries(String apiAccessKey, String redmineUrl) {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey(redmineUrl, apiAccessKey);
        TimeEntryManager timeEntryManager = mgr.getTimeEntryManager();
        Map<String, String> params = new HashMap<>(4);
        params.put("project_id", "bug_cause_analysis");
        params.put("limit", "101");
        ResultsWrapper<TimeEntry> entries = null;
        try {
            entries = timeEntryManager.getTimeEntries(params);
        } catch (RedmineException e) {
            e.printStackTrace();
        }
        if (entries == null) {
            return;
        }
        int totalFoundOnServer = entries.getTotalFoundOnServer();
        List<TimeEntry> results = entries.getResults();
        System.out.println(results);
    }

    /**
     * TODO
     * 查看问题
     *
     * @throws RedmineException
     */
    private static void queryIssue() throws RedmineException {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey("http://redmine-qa.mxnavi.com/", "1f905383da4f783bad92e22f430c7db0b15ae258");
        IssueManager issueManager = mgr.getIssueManager();
        Issue issueById = issueManager.getIssueById(589298, Include.attachments);
        System.out.println(issueById);
    }


    public static OkHttpClient login() {
        RequestBody body = new FormBody.Builder()
                .add("username", "qig")
                .add("password", "MAnsiontech^7")
                .add("authenticity_token", UUID.randomUUID().toString())
                .add("back_url", "http://redmine-qa.mxnavi.com/")
                .add("login", "登录 »")
                .build();
        Headers.Builder builder = new Headers.Builder();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(@NotNull HttpUrl httpUrl, @NotNull List<Cookie> cookies) {
                        if (cookies.size() > 0) {
                            COOKIE_STORE.put(getCacheKey(httpUrl), cookies);
                        }
                    }

                    @NotNull
                    @Override
                    public List<Cookie> loadForRequest(@NotNull HttpUrl httpUrl) {
                        List<Cookie> cookies = COOKIE_STORE.get(getCacheKey(httpUrl));
                        return cookies != null ? cookies : new ArrayList<>();
                    }
                })
                .build();

        Request post = new Request.Builder()
                .url(LOGIN_URL)
                .method("POST", body)
                .headers(builder.build())
                .build();

        try {
            client.newCall(post).execute();
            return client;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return client;
    }

    private static String getCacheKey(HttpUrl url) {
        return url.host() + ":" + url.port();
    }

//    public static void main(String[] args) {
//        OkHttpClient login = login();
//        System.out.println(login);
//    }
}
