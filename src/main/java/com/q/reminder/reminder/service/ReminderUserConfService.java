package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.ReminderUserConf;
import com.q.reminder.reminder.vo.UserReminderVo;
import tech.powerjob.worker.log.OmsLogger;

import java.util.List;


/**
 * redmine 设置不提醒写日报列表(ReminderUserConf)表服务接口
 *
 * @author makejava
 * @since 2023-09-06 16:52:29
 */
public interface ReminderUserConfService extends IService<ReminderUserConf>{

    void reminder(OmsLogger omsLogger);
}
