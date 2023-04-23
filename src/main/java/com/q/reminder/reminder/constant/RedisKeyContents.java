package com.q.reminder.reminder.constant;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.constant.RedisKeyContents
 * @Description :
 * @date :  2023.03.08 11:45
 */
public interface RedisKeyContents {
    String REDMINE_USERINFO_REDMINE_ALL = "redmine:userinfo:all";
    String REDMINE_PROJECT_ALL = "redmine:project:all";
    String REDMINE_PROJECT_KEY = "redmine:project";
    String TABLE_USER_CONFIG = "table:user:config";
    String FEISHU_MESSAGE_INVOKEEXCEEDEDTIMES = "feishu:message:invokeExceededTimes";
    String FEISHU_TENANT_ACCESS_TOKEN_KEY = "feishu:TENANT_ACCESS_TOKEN::expire";
}
