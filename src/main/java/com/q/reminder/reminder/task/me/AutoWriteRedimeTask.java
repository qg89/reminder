package com.q.reminder.reminder.task.me;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.util.SystemUtils;
import com.q.reminder.reminder.util.selenium.EdgeSeleniumUtils;
import com.taskadapter.redmineapi.RedmineManager;
import com.taskadapter.redmineapi.bean.TimeEntry;
import com.taskadapter.redmineapi.internal.Transport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.math.BigDecimal;
import java.net.HttpCookie;
import java.util.List;
import java.util.Map;
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
    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger log = context.getOmsLogger();
        String jobParams = context.getJobParams();
        ProcessResult result = new ProcessResult(true);
        DateTime time = DateUtil.yesterday();
        String dateTime = time.toString("yyyy-MM-dd");
        if (StringUtils.isNotBlank(jobParams) && isValidDateFormat(jobParams, "yyyy-MM-dd")) {
            dateTime = DateUtil.parse(jobParams).toString("yyyy-MM-dd");
        }
        log.info("日期：{}", dateTime);
        String path;
        if (SystemUtils.isLinux()) {
            path = "/usr/drive/geckodriver";
        } else  {
            path = "D:\\Users\\Administrator\\Desktop\\webDrive\\msedgedriver.exe";
        }
        String cookie = EdgeSeleniumUtils.cookie(path);
        log.info("cookie:{}", cookie);
        String body = HttpUtil.createGet("https://redmine-pa.mxnavi.com/issues/38668/time_entries/autocomplete_for_time?q=" + dateTime).addHeaders(Map.of("Cookie", cookie)).execute().body();
        if (StringUtils.isBlank(body)) {
            log.info("body 为空");
            return result;
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
            return result;
        }
        RProjectInfo info = new RProjectInfo();
        info.setRedmineType("2");
        info.setPmKey("e47f8dbff40521057e2cd7d6d0fed2765d474d4f");
        RedmineManager mgr = RedmineApi.getRedmineManager(info);
        Transport transport = mgr.getTransport();
        TimeEntry timeEntry = new TimeEntry(transport);
        timeEntry.setHours(hours);
        timeEntry.setProjectId(260);
        timeEntry.setSpentOn(time.toJdkDate());
        timeEntry.setIssueId(38668);
        mgr.getTimeEntryManager().createTimeEntry(timeEntry);
        log.info("当日已更新完成");
        return result;
    }

    private String cookie() {
        String name;
        String value;
        HttpResponse execute = HttpUtil.createGet("https://redmine-pa.mxnavi.com/login").execute();
        List<HttpCookie> cookies = execute.getCookies();
        HttpCookie httpCookie = cookies.get(0);
        name = httpCookie.getName();
        value = httpCookie.getValue();
        return name + "=" + value;
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
}
