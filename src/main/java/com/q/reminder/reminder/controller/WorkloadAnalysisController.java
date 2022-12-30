package com.q.reminder.reminder.controller;

import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.service.WGroupService;
import com.q.reminder.reminder.service.WRoleGroupUserService;
import com.q.reminder.reminder.service.WRoleService;
import com.q.reminder.reminder.vo.OptionVo;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.WorkloadAnalysisController
 * @Description :
 * @date :  2022.12.28 09:47
 */
@RestController
@RequestMapping("/workload")
public class WorkloadAnalysisController {

    @Autowired
    private WRoleService wRoleService;
    @Autowired
    private RedmineUserInfoService redmineUserInfoService;
    @Autowired
    private WGroupService wGroupService;
    @Autowired
    private WRoleGroupUserService wRoleGroupUserService;

    @GetMapping("/option")
    public ReturnT<List<OptionVo>> option(WorkloadParamsVo paramsVo) {
        String pKey = paramsVo.getPKey();
        String year = paramsVo.getYear();
        if (StringUtils.isBlank(pKey) || StringUtils.isBlank(year)) {
            return new ReturnT<>(new ArrayList<>());
        }
        return new ReturnT<>(wRoleGroupUserService.option(paramsVo));
    }

    /**
     * 角色投入（人/月）
     *
     * @param params
     * @return
     */
    @GetMapping("/role_involvement")
    public ReturnT<List<RoleInvolvementVo>> roleInvolvement(WorkloadParamsVo params) {
        return new ReturnT<>(wRoleService.roleInvolvement(params));
    }

    /**
     * 年工作强度
     *
     * @return
     */
    @GetMapping("/working_intensity")
    public ReturnT<List<RoleInvolvementVo>> workingIntensity(WorkloadParamsVo params) {
        return new ReturnT<>(redmineUserInfoService.roleInvolvement(params));
    }

    /**
     * 剩余工作量
     *
     * @param params
     * @return
     */
    @GetMapping("/residual_workload")
    public ReturnT<List<RoleInvolvementVo>> residualWorkload(WorkloadParamsVo params) {
        return new ReturnT<>(redmineUserInfoService.residualWorkload(params));
    }

    /**
     * 按分组分类
     *
     * @param params
     * @return
     */
    @GetMapping("/group_workload")
    public ReturnT<List<RoleInvolvementVo>> groupWorkload(WorkloadParamsVo params) {
        return new ReturnT<>(wGroupService.groupWorkload(params));
    }

    /**
     * 按角色分类
     *
     * @param params
     * @return
     */
    @GetMapping("/group_user_workload")
    public ReturnT<List<RoleInvolvementVo>> groupUserWorkload(WorkloadParamsVo params) {
        return new ReturnT<>(redmineUserInfoService.groupUserWorkload(params));
    }
}
