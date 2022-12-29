package com.q.reminder.reminder.controller;

import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.service.WRoleService;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 角色投入（人/月）
     * @param pId
     * @param year
     * @return
     */
    @GetMapping("/role_involvement/{pId}/{year}")
    public ReturnT<Object> roleInvolvement(@PathVariable("pId") String pId, @PathVariable("year") String year) {
        return new ReturnT<>(wRoleService.roleInvolvement(pId, year));
    }

    /**
     * 年工作强度
     * @param pId
     * @param year
     * @return
     */
    @GetMapping("/working_intensity/{pId}/{year}")
    public ReturnT<Object> workingIntensity(@PathVariable("pId") String pId, @PathVariable("year") String year) {
        return new ReturnT<>(redmineUserInfoService.roleInvolvement(pId, year));
    }

    /**
     * 剩余工作量
     * @param pId
     * @param year
     * @return
     */
    @GetMapping("/residual_workload/{pId}/{year}")
    public ReturnT<Object> residualWorkload(@PathVariable("pId") String pId, @PathVariable("year") String year) {
        return new ReturnT<>(redmineUserInfoService.residualWorkload(pId, year));
    }
}
