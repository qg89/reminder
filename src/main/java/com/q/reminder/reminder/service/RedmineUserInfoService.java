package com.q.reminder.reminder.service;

import com.github.jeffreyning.mybatisplus.service.IMppService;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.service.RedmineUserInfoService
 * @Description :
 * @date :  2022.10.27 16:20
 */
public interface RedmineUserInfoService extends IMppService<RedmineUserInfo> {
    List<RoleInvolvementVo> roleInvolvement(WorkloadParamsVo paramsVo);

    List<RoleInvolvementVo> residualWorkload(WorkloadParamsVo paramsVo);

    List<RoleInvolvementVo> groupUserWorkload(WorkloadParamsVo paramsVo);

    List<RedmineUserInfo> listUsers(String redmineType);

    List<RedmineUserInfo> listUserAll();

    void saveOrupdateMultiIdAll(List<RedmineUserInfo> data);
}
