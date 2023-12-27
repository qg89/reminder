package com.q.reminder.reminder.util.selenium;

import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.selenium.Common
 * @Description :
 * @date :  2023.12.25 17:30
 */
public class Common1 {
    /**
     * edge添加参数
     * @param options
     * @return
     */
    public static FirefoxOptions addArguments(FirefoxOptions options){
        options.addArguments("disable-infobars");
        // 浏览器不提供可视化页面. linux下如果系统不支持可视化不加这条会启动失败
        options.addArguments("--headless");
        // 启动无沙盒模式运行，以最高权限运行
        options.addArguments("--no-sandbox");
        // 优化参数
        // 不加载图片, 提升速度
        options.addArguments("blink-settings=imagesEnabled=false");
        options.addArguments("--disable-dev-shm-usage");
        // 禁用gpu渲染
        options.addArguments("--disable-gpu");

        // 禁用阻止弹出窗口
        options.addArguments("--disable-popup-blocking");
        // 禁用扩展
        options.addArguments("disable-extensions");
        // 禁用JavaScript
        options.addArguments("--disable-javascript");
        // 1、1解决403错误
//        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
        // 默认浏览器检查
        options.addArguments("no-default-browser-check");
        Map<String, Object> prefs = new HashMap();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        return options;
    }

}
