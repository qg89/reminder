package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.WUserTimes;
import com.q.reminder.reminder.vo.ProjectUserTimeVo;

import java.util.List;
import java.util.Map;


/**
 * (WUserTimes)表服务接口
 *
 * @author makejava
 * @since 2022-12-28 11:32:44
 */
public interface WUserTimesService extends IService<WUserTimes>{

    List<Map<String, String>> listByTable(String day, String dateType);
}
