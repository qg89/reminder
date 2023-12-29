package com.q.reminder.reminder.task.me;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpUtil;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.task.me.entity.AutoWriteRedmineUserInfoVo;
import com.q.reminder.reminder.util.HolidayUtils;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.util.SystemUtils;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.internal.Transport;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.me.AutoWriteRedimeTask
 * @Description :
 * @date :  2023.12.22 11:43
 */
@Component
public class AutoWriteRedimeTask implements BasicProcessor {
    public static String getCookie(OmsLogger log, DesiredCapabilities dc) {
        RemoteWebDriver webDriver = null;
        String cookie;
        try {
            URI uri = URI.create("http://192.168.3.46:4444");
            webDriver = new RemoteWebDriver(uri.toURL(), dc);
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
            log.error(e.getMessage());
        } finally {
            if (webDriver != null) {
                webDriver.quit();
            }
        }
        return null;
    }

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger log = context.getOmsLogger();
        String jobParams = context.getJobParams();
        ProcessResult result = new ProcessResult(true);

        String dateTime = DateUtil.yesterday().toString("yyyy-MM-dd");
        if (StringUtils.isNotBlank(jobParams) && isValidDateFormat(jobParams, "yyyy-MM-dd")) {
            dateTime = DateUtil.parse(jobParams).toString("yyyy-MM-dd");
        }
        log.info("日期：{}", dateTime);
        if (HolidayUtils.isHoliday(dateTime)) {
            log.info("非工作日");
            return result;
        }

        List<AutoWriteRedmineUserInfoVo> list = new ArrayList<>();

        AutoWriteRedmineUserInfoVo vo = new AutoWriteRedmineUserInfoVo();
        vo.setSpentOn(dateTime);
        vo.setIssueId(38668);
        vo.setProjectId(260);
        vo.setPmKey("e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
        vo.setName("齐钢");
        list.add(vo);
        vo.setIssueId(35201);
        vo.setPmKey("46b97ae85a3d42da0879c63ec292a2b3afc011c9");
        vo.setName("徐鹏超");
        list.add(vo);
        list.forEach(e -> {
            try {
                autoWrite(log, e);
            } catch (RedmineException ex) {
                throw new RuntimeException(ex);
            } catch (MalformedURLException ex) {
                throw new RuntimeException(ex);
            }
        });
        return result;
    }

    private static void autoWrite(OmsLogger log, AutoWriteRedmineUserInfoVo userInfoVo) throws RedmineException, MalformedURLException {
        String name = userInfoVo.getName();
        log.info("开始执行----------------------------{}", name);
        String spentOn = userInfoVo.getSpentOn();
        String path;
        String cookie;
        DesiredCapabilities dc = new DesiredCapabilities();
        dc.setBrowserName("chrome");
        if (SystemUtils.isLinux()) {
            dc.setPlatform(Platform.LINUX);
        } else {
            dc.setPlatform(Platform.WIN11);
        }
        cookie = getCookie(log, dc);
        log.info("cookie:{}", cookie);
        String body = HttpUtil.createGet("https://redmine-pa.mxnavi.com/issues/38668/time_entries/autocomplete_for_time?q=" + spentOn).addHeaders(Map.of("Cookie", cookie)).execute().body();
        if (StringUtils.isBlank(body)) {
            log.info("body 为空");
            return;
        }
        log.info("body:{}", body);
        Pattern pattern = Pattern.compile("\\d+\\.\\d+");
        Matcher matcher = pattern.matcher(body);
        double spendOn = 0.00D;
        double estimateOn = 0.00D;
        if (matcher.find() && NumberUtil.isNumber(matcher.group())) {
            spendOn = Double.valueOf(matcher.group());
            log.info("当日耗时：{}", spendOn);
        }
        if (matcher.find() && NumberUtil.isNumber(matcher.group())) {
            estimateOn = Double.valueOf(matcher.group());
            log.info("当天在岗预估时间：{}", spendOn);
        }
        float hours = BigDecimal.valueOf(estimateOn).subtract(BigDecimal.valueOf(spendOn)).floatValue();
        if (hours <= 0) {
            log.info("耗时:0,{}", name);
            return;
        }
        RProjectInfo info = new RProjectInfo();
        info.setRedmineType("2");
        info.setPmKey(userInfoVo.getPmKey());
        RedmineManager mgr = RedmineApi.getRedmineManager(info);
        Transport transport = mgr.getTransport();
        TimeEntry timeEntry = new TimeEntry(transport);
        timeEntry.setHours(hours);
        timeEntry.setProjectId(userInfoVo.getProjectId());
        timeEntry.setSpentOn(DateUtil.parse(spentOn, "yyyy-MM-dd").toJdkDate());
        timeEntry.setIssueId(userInfoVo.getIssueId());
        mgr.getTimeEntryManager().createTimeEntry(timeEntry);
        log.info("当日已更新完成");
    }

    public static boolean isValidDateFormat(String dateStr, String dateFormat) {
        if (ObjectUtil.isEmpty(dateStr)) {
            return false;
        }
        try {
            DateUtil.parse(dateStr, dateFormat); // 将字符串解析为日期对象，如果解析成功，则说明字符串是有效的日期格式；否则说明字符串不是有效的日期格式。
            return true;
        } catch (Exception e) {
            return false;
        }
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
