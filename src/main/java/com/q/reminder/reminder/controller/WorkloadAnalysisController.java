package com.q.reminder.reminder.controller;

import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.service.WRoleService;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 角色投入（人/月）
     *
     * @param pKey
     * @param year
     * @return
     */
    @GetMapping("/role_involvement/{pKey}/{year}")
    public ReturnT<Object> roleInvolvement(@PathVariable("pKey") String pKey, @PathVariable("year") String year) {
        return new ReturnT<>(wRoleService.roleInvolvement(pKey, year));
    }

    /**
     * 年工作强度
     *
     * @param pKey
     * @param year
     * @return
     */
    @GetMapping("/working_intensity/{pKey}/{year}")
    public ReturnT<Object> workingIntensity(@PathVariable("pKey") String pKey, @PathVariable("year") String year) {
        return new ReturnT<>(redmineUserInfoService.roleInvolvement(pKey, year));
    }

    /**
     * 剩余工作量
     *
     * @param pKey
     * @param year
     * @return
     */
    @GetMapping("/residual_workload/{pKey}/{year}")
    public ReturnT<Object> residualWorkload(@PathVariable("pKey") String pKey, @PathVariable("year") String year) {
        return new ReturnT<>(redmineUserInfoService.residualWorkload(pKey, year));
    }
}
