package com.q.reminder.reminder.util;

import com.q.reminder.reminder.ReminderApplication;
import org.springframework.boot.system.ApplicationHome;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.ResourceUtils
 * @Description :
 * @date :  2022.11.10 09:26
 */
public abstract class ResourceUtils {

    public static String path(String resource) {
        ApplicationHome applicationHome = new ApplicationHome(ReminderApplication.class);
        String parent = applicationHome.getSource().getParent();
        return parent + "/resources/" + resource;
    }
}
