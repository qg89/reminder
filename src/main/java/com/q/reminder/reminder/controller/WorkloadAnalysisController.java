package com.q.reminder.reminder.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.vo.OptionVo;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.UserTimeMonthRatioVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private WUserTimeMonthService wUserTimeMonthService;
    @Autowired
    private ProjectInfoService projectInfoService;

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
     * 项目下拉列表
     * @return
     */
    @GetMapping("/project_option")
    public ReturnT<List<RProjectInfo>> projectOption() {
        LambdaQueryWrapper<RProjectInfo> lq = Wrappers.lambdaQuery();
        lq.select(RProjectInfo::getId, RProjectInfo::getPname);
        return new ReturnT<>(projectInfoService.list(lq));
    }

    /**
     * 人员下拉列表
     * @return
     */
    @GetMapping("/user_option")
    public ReturnT<List<RedmineUserInfo>> userOption() {
        LambdaQueryWrapper<RedmineUserInfo> lq = Wrappers.lambdaQuery();
        lq.select(RedmineUserInfo::getAssigneeId, RedmineUserInfo::getAssigneeName);
        return new ReturnT<>(redmineUserInfoService.list(lq));
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
     * 工作强度
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
     * 按组别分类
     *
     * @param params
     * @return
     */
    @GetMapping("/group_workload")
    public ReturnT<List<RoleInvolvementVo>> groupWorkload(WorkloadParamsVo params) {
        return new ReturnT<>(wGroupService.groupWorkload(params));
    }

    /**
     * 按组别角色分类
     *
     * @param params
     * @return
     */
    @GetMapping("/group_user_workload")
    public ReturnT<List<RoleInvolvementVo>> groupUserWorkload(WorkloadParamsVo params) {
        return new ReturnT<>(redmineUserInfoService.groupUserWorkload(params));
    }

    /**
     * 年投入比例
     * @param params
     * @return
     */
    @GetMapping("/input_ratio")
    public ReturnT<List<RoleInvolvementVo>> inputRatio(WorkloadParamsVo params) {
        return new ReturnT<>(wUserTimeMonthService.inputRatio(params));
    }

    /**
     * 年投入比例
     * @param list
     * @return
     */
    @PostMapping("/input_ratio_edit")
    public ReturnT<Boolean> inputRatioEdit(@RequestBody List<UserTimeMonthRatioVo> list) {
        return new ReturnT<>(wUserTimeMonthService.inputRatioEdit(list));
    }
}
