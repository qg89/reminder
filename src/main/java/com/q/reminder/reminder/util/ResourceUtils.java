package com.q.reminder.reminder.util;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.ResourceUtils
 * @Description :
 * @date :  2022.11.29 16:41
 */
public class ResourceUtils {

    public static String path() {
        return ResourceUtils.class.getResource("/file/logo.jpg").getFile();
    }
}
