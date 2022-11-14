package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.MemberInfoService
 * @Description :
 * @date :  2022.09.27 13:23
 */
public interface ProjectInfoService extends IService<ProjectInfo> {
    List<WeeklyProjectVo> getWeeklyDocxList(int weekNumber, String pKey);

}
