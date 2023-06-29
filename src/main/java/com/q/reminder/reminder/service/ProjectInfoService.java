package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.vo.ProjectInfoVo;
import com.q.reminder.reminder.vo.RProjectReaVo;
import com.q.reminder.reminder.vo.WeeklyByProjectVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;

import java.util.List;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.MemberInfoService
 * @Description :
 * @date :  2022.09.27 13:23
 */
public interface ProjectInfoService extends IService<RProjectInfo> {
    List<WeeklyProjectVo> getWeeklyDocxList(int weekNumber, String pKey);

    List<WeeklyByProjectVo> weeklyByProjectList(String pKey, String name);

    List<List<ProjectInfoVo>> listToArray(List<RProjectInfo> list, Map<String, String> userMap, Map<String, String> groupMap, Map<String, Double> projectMap);

    List<ProjectInfoVo> listInfo();

    List<RProjectInfo> listAll();

    void updateInfo(RProjectReaVo info);

    RProjectInfo projectInfoByPrjctKey(String prjctKey);

    Map<String, Double> getProjectCost();

}
