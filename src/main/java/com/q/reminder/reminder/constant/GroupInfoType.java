package com.q.reminder.reminder.constant;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.constant.GroupInfoType
 * @Description :
 * @date :  2023/6/29 11:01
 */
public interface GroupInfoType {
    //-1，无
    //0，三部日常提醒
    //1，redmine需求提醒
    //2，部门群
    int GROUP_NONE = -1;
    int DAILY_REMINDERS = 0;
    int FEATURE_REMINDERS = 0;
    int DEP_GROUP = 2;
}
