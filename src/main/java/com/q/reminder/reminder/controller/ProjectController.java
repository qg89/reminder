package com.q.reminder.reminder.controller;

import com.q.reminder.reminder.entity.GroupInfo;
import com.q.reminder.reminder.entity.GroupProject;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.UserP;
import com.q.reminder.reminder.service.GroupInfoService;
import com.q.reminder.reminder.service.GroupProjectService;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.UserPService;
import com.q.reminder.reminder.vo.ProjectReaVo;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.ProjectController
 * @Description :
 * @date :  2022.12.21 19:13
 */
@RestController
@RequestMapping("/p")
public class ProjectController {
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private GroupProjectService groupProjectService;
    @Autowired
    private UserPService userPService;


    @PostMapping("/save")
    public ReturnT<String> save(ProjectInfo info) {
        projectInfoService.save(info);
        return ReturnT.SUCCESS;
    }

    @PostMapping("/rea")
    public ReturnT<String> rea(ProjectReaVo vo) {
        groupProjectService.save(new GroupProject(vo.getChatId(), vo.getPId()));
        userPService.save(new UserP(vo.getUserId(), vo.getPId()));
        return ReturnT.SUCCESS;
    }
}
