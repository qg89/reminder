package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
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
public interface RedmineUserInfoService extends IService<RedmineUserInfo>{
    List<RoleInvolvementVo> roleInvolvement(WorkloadParamsVo paramsVo);

    List<RoleInvolvementVo> residualWorkload(WorkloadParamsVo paramsVo);

    List<RoleInvolvementVo> groupUserWorkload(WorkloadParamsVo paramsVo);
}
