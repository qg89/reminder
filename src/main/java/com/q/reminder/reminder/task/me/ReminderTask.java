package com.q.reminder.reminder.task.me;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nlf.calendar.Holiday;
import com.nlf.calendar.util.HolidayUtil;
import com.q.reminder.reminder.constant.FeiShuContents;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.MessageVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import java.util.Date;
import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.task.me.ReminderTask
 * @Description : 前2天的日报
 * @date :  2023/8/17 10:57
 */
@Component
@AllArgsConstructor
public class ReminderTask implements BasicProcessor {

    private final RdTimeEntryService rdTimeEntryService;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {
        OmsLogger omsLogger = context.getOmsLogger();
        ProcessResult result = new ProcessResult(true);
        DateTime dateTime = DateUtil.offsetDay(new Date(), -1);
        // 判断周末
        while (DateUtil.isWeekend(dateTime)) {
            dateTime.offset(DateField.DAY_OF_MONTH, -1);
        }
        String date = dateTime.toString("yyyy-MM-dd");
        Holiday holiday = HolidayUtil.getHoliday(date);
        if (holiday != null && holiday.isWork()) {
            List<RdTimeEntry> list = rdTimeEntryService.list(Wrappers.<RdTimeEntry>lambdaQuery().eq(RdTimeEntry::getSpentOn, date).eq(RdTimeEntry::getUserid, 1215));
            double value = list.stream().mapToDouble(RdTimeEntry::getHours).sum();
            if (value < 8) {
                MessageVo sendVo = new MessageVo();
                sendVo.setReceiveId(FeiShuContents.ADMIN_MEMBERS);
                sendVo.setContent("Hi 同学，该写日报了，" + date + "！已填日报：" + value + " 小时");
                BaseFeishu.message().sendText(sendVo, omsLogger);
            } else {
                omsLogger.info("日报已填写~！ date：{}, value：{}", date, value);
            }
        }
        return result;
    }
}
