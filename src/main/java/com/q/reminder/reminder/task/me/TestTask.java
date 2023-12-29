package com.q.reminder.reminder.task.me;

import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.net.URI;
import java.util.Set;


/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.me.Test
 * @Description :
 * @date :  2023.12.29 18:06
 */
@Component
public class TestTask implements BasicProcessor {

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger logger = context.getOmsLogger();
        WebDriver webDriver = null;
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setBrowserName("chrome");
        dc.setPlatform(Platform.LINUX);
        try {
            URI uri = URI.create("http://192.168.3.46/:4444/wd/hub");
            webDriver = new RemoteWebDriver(uri.toURL(), dc);
            // 1.模拟打开登陆页面
            String loginUrl = "https://redmine-pa.mxnavi.com/login";
            logger.info("打开登录页面,地址是{}", loginUrl);
            webDriver.get(loginUrl);
            // 2.等3秒钟响应后再操作，不然内容可能还没有返回
            Thread.sleep(3000L);

            // xpath 输入框元素的绝对路径
            // 3.找到账号的输入框，并模拟输入账号
            WebElement accountInput = webDriver.findElement(By.id("username"));
            accountInput.sendKeys("qig");
            logger.info("开始输入账号...");
            Thread.sleep(1000L);
            // 4.找到密码的输入框，并模拟输入密码
            WebElement passwordInput = webDriver.findElement(By.id("password"));
            passwordInput.sendKeys("MAnsiontech^7");
            logger.info("开始输入密码...");
            Thread.sleep(1000L);
            // 5.找到登陆的按钮，并模拟点击登陆
            WebElement loginButton = webDriver.findElement(By.id("login-submit"));
            loginButton.click();
            logger.info("开始点击登录...");
            Thread.sleep(3000L);
            String s = doSomeThing(webDriver);
            logger.info(s);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
        return new ProcessResult(true);
    }

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
