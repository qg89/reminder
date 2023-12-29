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
import com.q.reminder.reminder.util.selenium.EdgeSeleniumUtils;
import com.q.reminder.reminder.util.selenium.FirefoxSeleniumUtils;
import com.taskadapter.redmineapi.RedmineException;
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
import java.util.ArrayList;
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
            }
        });
        return result;
    }

    private static void autoWrite(OmsLogger log, AutoWriteRedmineUserInfoVo userInfoVo) throws RedmineException {
        String name = userInfoVo.getName();
        log.info("开始执行----------------------------{}", name);
        String spentOn = userInfoVo.getSpentOn();
        String path;
        String cookie;
        if (SystemUtils.isLinux()) {
            path = "/usr/drive/geckodriver";
            cookie = FirefoxSeleniumUtils.cookie(path);
        } else {
            path = "D:\\dev_tools\\webDrive\\msedgedriver.exe";
            cookie = EdgeSeleniumUtils.cookie(path);
        }
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
}
