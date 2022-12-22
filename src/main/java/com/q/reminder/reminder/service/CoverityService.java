package com.q.reminder.reminder.service;

import com.github.yulichang.base.MPJBaseService;
import com.q.reminder.reminder.entity.Coverity;
import com.q.reminder.reminder.vo.CoverityAndRedmineSaveTaskVo;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.CoverityService
 * @Description :
 * @date :  2022.12.01 10:36
 */
public interface CoverityService extends MPJBaseService<Coverity> {
    List<CoverityAndRedmineSaveTaskVo> queryByProject(String pKey);
}
