package com.q.reminder.reminder.service;

import com.q.reminder.reminder.vo.WeeklyVo;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.WeeklyService
 * @Description :
 * @date :  2022.11.15 16:46
 */
public interface WeeklyService {

    void resetReport(WeeklyVo vo) throws Exception;
}
