package com.q.reminder.reminder.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.entity.ReminderUserConf;
import com.q.reminder.reminder.mapper.ReminderUserConfMapping;
import com.q.reminder.reminder.service.ReminderUserConfService;
import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.vo.MessageVo;
import com.q.reminder.reminder.vo.UserReminderVo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import tech.powerjob.worker.log.OmsLogger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * redmine 设置不提醒写日报列表(ReminderUserConf)表服务实现类
 *
 * @author makejava
 * @since 2023-09-06 16:52:33
 */
@AllArgsConstructor
@Service
public class ReminderUserConfServiceImpl extends ServiceImpl<ReminderUserConfMapping, ReminderUserConf> implements ReminderUserConfService {

    @Override
    public void reminder(OmsLogger omsLogger) {
        final int normal = 8;
        DateTime now = DateTime.now();
        // 开始日期
        DateTime dateTime = now.offsetNew(DateField.DAY_OF_MONTH, -7);
        List<UserReminderVo> userReminderList = baseMapper.listByUser(dateTime.toString("yyyy-MM-dd"));
//        userReminderList.removeIf(e -> {
//            String memberId = null;
//            boolean flag = Objects.equals("1", e.getEnable());
//            if (flag) {
//                return true;
//            }
//            Date toDay = new DateTime(now.toString("yyyy-MM-dd"));
//            Date startDate = e.getStartDate();
//            Date endDate = e.getEndDate();
//            // 当天在开始结束范围内，过滤掉
//            if (startDate != null && endDate != null && DateUtil.isIn(toDay, startDate, endDate)) {
//                omsLogger.info("不提醒，[{}]当前为休假期，startDate：{} ~ endDate：{}", e.getUserName(), startDate, endDate);
//                return true;
//            }
//            return false;
//        });
        if (CollectionUtils.isEmpty(userReminderList)) {
            omsLogger.info("当前日期全部填写日报！");
            return;
        }

        Map<String, StringBuffer> contentMap = new HashMap<>();
        // 按人分组
        userReminderList.stream().collect(Collectors.groupingBy(UserReminderVo::getMemberId)).forEach((memberId, list) -> {
            UserReminderVo userReminderVo = list.get(0);
            if (userReminderVo == null) {
                omsLogger.info("获取用户信息为空！");
                return;
            }
            Date startDate = userReminderVo.getStartDate();
            Date endDate = userReminderVo.getEndDate();
            StringBuffer content = new StringBuffer("Hi 同学，该写日报了！").append("\r\t\n");
            // 按日期
            Map<String, List<UserReminderVo>> spentMap = list.stream().collect(Collectors.groupingBy(UserReminderVo::getSpentOn));
            // sort
            Map<String, List<UserReminderVo>> resultMap = new LinkedHashMap<>();
            spentMap.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(e -> resultMap.put(e.getKey(), e.getValue()));
            AtomicInteger i = new AtomicInteger();
            resultMap.forEach((date, userList) -> {
                // 休假不提醒
                if (startDate != null && endDate != null && DateUtil.isIn(new DateTime(date).toJdkDate(), startDate, endDate)) {
                    omsLogger.info("不提醒，[{}]当前为休假期，startDate：{} ~ endDate：{}", userReminderVo.getUserName(), startDate, endDate);
                    return;
                }
                // 当日日报合计
                double sumDay = userList.stream().filter(e -> e.getHours() != null).mapToDouble(UserReminderVo::getHours).sum();
                // 判断是否 < 8
                if (sumDay < normal) {
                    i.getAndIncrement();
                    content.append(date).append("！已填日报：").append(sumDay).append(" 小时").append("\r\t\n");
                }
                omsLogger.info("用户：{}， 日期：{}", userReminderVo.getUserName(), date);
            });
            if (i.get() > 0) {
                contentMap.put(memberId, content);
            }
        });
        contentMap.forEach((memberId, content) -> {
            MessageVo sendVo = new MessageVo();
            sendVo.setReceiveId(memberId);
            sendVo.setContent(content.toString());
            BaseFeishu.message().sendText(sendVo, omsLogger);
        });
    }
}
