package com.q.reminder.reminder.strategys.service.impl;

import com.q.reminder.reminder.strategys.service.RedmineTypeStrategy;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.OldRemineStrategy
 * @Description :
 * @date :  2023.03.27 10:37
 */
public abstract class AbstractRedmineStrategy implements RedmineTypeStrategy {

    @Override
    public Boolean createIssue() {
        return Boolean.TRUE;
    }
}
