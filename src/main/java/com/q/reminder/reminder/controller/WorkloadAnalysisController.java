package com.q.reminder.reminder.controller;

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

    @GetMapping("/role_involvement/{pId}/{year}")
    public ReturnT<Object> roleInvolvement(@PathVariable("pId") String pId, @PathVariable("year") String year) {
        return new ReturnT<>(wRoleService.roleInvolvement(pId, year));
    }
}
