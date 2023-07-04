package com.q.reminder.reminder.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.vo.OvertimeVo;

import java.util.List;


/**
 * (RdTimeEntry)表服务接口
 *
 * @author makejava
 * @since 2023-01-19 11:47:40
 */
public interface RdTimeEntryService extends IMppService<RdTimeEntry>{

    List<OvertimeVo> listOvertime(String ym);
}
