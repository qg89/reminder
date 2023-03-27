package com.q.reminder.reminder.test;

import com.taskadapter.redmineapi.IssueManager;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.RedmineManagerFactory;
import com.taskadapter.redmineapi.bean.Issue;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.test.TestReminder
 * @Description :
 * @date :  2023.03.24 10:46
 */
public class TestReminder {
    public static void main(String[] args) throws RedmineException {
        RedmineManager mgr = RedmineManagerFactory.createWithApiKey("http://redmine-pa.mxnavi.com",
                "e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
        IssueManager issueManager = mgr.getIssueManager();
        Issue issueById = issueManager.getIssueById(7070);
        System.out.println(issueById);
    }
}
