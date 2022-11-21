package com.q.reminder.reminder.util;

import com.q.reminder.reminder.ReminderApplication;
import org.springframework.boot.system.ApplicationHome;

import java.io.File;

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
        String path = applicationHome.getSource().getParent()+ "/resources/" + resource;
        File file = new File(path);
        if (!file.isDirectory()) {
            file.mkdir();
        }
        return path ;
    }
}
