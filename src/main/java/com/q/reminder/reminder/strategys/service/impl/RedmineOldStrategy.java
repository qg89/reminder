package com.q.reminder.reminder.strategys.service.impl;

import com.q.reminder.reminder.strategys.anno.RedmineTypeAnnotation;
import com.q.reminder.reminder.enums.ReminderTypeEnum;
import org.springframework.stereotype.Component;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.impl.RedmineOldStrategy
 * @Description :
 * @date :  2023.03.27 10:40
 */
@Component
@RedmineTypeAnnotation(type = ReminderTypeEnum.OLD)
public class RedmineOldStrategy extends AbstractRedmineStrategy {

    @Override
    public Boolean createIssue()  {
        return false;
    }
}
