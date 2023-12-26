package com.q.reminder.reminder.util.selenium;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Set;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.selenium.EdgeSeleniumUtils
 * @Description :
 * @date :  2023.12.26 18:10
 */
@Log4j2
public class FirefoxSeleniumUtils {

    public static String cookie(String webdriverPath) {
        WebDriver webDriver = null;
        try {
            // 设置 chromedirver 的存放位置
            System.getProperties().setProperty("webdriver.firefox.driver", webdriverPath);
            FirefoxOptions firefoxOptions = new FirefoxOptions();
            Common1.addArguments(firefoxOptions);
            // 实例化
            webDriver = new FirefoxDriver(firefoxOptions);
            // 1.模拟打开登陆页面
            String loginUrl = "https://redmine-pa.mxnavi.com/login";
            log.info("打开登录页面,地址是{}", loginUrl);
            webDriver.get(loginUrl);
            // 2.等3秒钟响应后再操作，不然内容可能还没有返回
            Thread.sleep(3000L);
            // xpath 输入框元素的绝对路径
            // 3.找到账号的输入框，并模拟输入账号
            WebElement accountInput = webDriver.findElement(By.id("username"));
            accountInput.sendKeys("qig");
            log.info("开始输入账号...");
            Thread.sleep(1000L);
            // 4.找到密码的输入框，并模拟输入密码
            WebElement passwordInput = webDriver.findElement(By.id("password"));
            passwordInput.sendKeys("MAnsiontech^7");
            log.info("开始输入密码...");
            Thread.sleep(1000L);
            // 5.找到登陆的按钮，并模拟点击登陆
            WebElement loginButton = webDriver.findElement(By.id("login-submit"));
            loginButton.click();
            log.info("开始点击登录...");
            Thread.sleep(3000L);
           return doSomeThing(webDriver);
        } catch (Exception e) {
            log.error(e);
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
        return null;
    }


    //    public static void doSomeThing(WebDriver webDriver) {
//        // 获得LocalStorge里的数据
//        WebStorage webStorage = (WebStorage) new Augmenter().augment(webDriver);
//        LocalStorage localStorage = webStorage.getLocalStorage();
//        String username = localStorage.getItem("username");
//        System.out.println("username:" + username);
//        // 获得SessionStorge里的数据
//        SessionStorage sessionStorage = webStorage.getSessionStorage();
//        String vuex = sessionStorage.getItem("vuex");
//        System.out.println("vuex:" + vuex);
//    }
    private static String doSomeThing(WebDriver webDriver) {
        // 获取cookie
        Set<Cookie> coo = webDriver.manage().getCookies();
        StringBuilder cookies = new StringBuilder();
        if (coo != null) {
            for (Cookie cookie : coo) {
                String name = cookie.getName();
                String value = cookie.getValue();
                cookies.append(name).append("=").append(value).append("; ");
            }
        }
        return cookies.toString();
    }
}
