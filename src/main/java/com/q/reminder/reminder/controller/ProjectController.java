package com.q.reminder.reminder.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.*;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.vo.ProjectReaVo;
import com.xxl.job.core.biz.model.ReturnT;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private CoverityService coverityService;
    @Autowired
    private GroupInfoService groupInfoService;
    @Autowired
    private LoginService loginService;

    @GetMapping("/i")
    public ReturnT<List<ProjectInfo>> list() {
        LambdaQueryWrapper<ProjectInfo> lq = Wrappers.lambdaQuery();
        lq.orderByDesc(ProjectInfo::getCreateTime);
        List<ProjectInfo> list = projectInfoService.list(lq);
        return new ReturnT<>(list);
    }

    @PostMapping("/s")
    public ReturnT<String> save(@RequestBody ProjectReaVo info) {
        projectInfoService.save(info);
        if (!saveRea(info)) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @PostMapping("/e")
    public ReturnT<String> edit(@RequestBody ProjectReaVo info) {
        projectInfoService.updateById(info);
        if (!saveRea(info)) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @PostMapping("/r")
    public ReturnT<String> rea(@RequestBody ProjectReaVo vo) {
        if (!saveRea(vo)) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    private Boolean saveRea(ProjectReaVo vo) {
        String pId = vo.getPId();
        String chatId = vo.getChatId();
        String userId = vo.getUserId();
        String cProjectId = vo.getCProjectId();
        if (StringUtils.isBlank(pId) || projectInfoService.getById(pId) == null) {
            return Boolean.FALSE;
        }
        if (StringUtils.isNotBlank(chatId)) {
            groupProjectService.saveOrUpdateByMultiId(new GroupProject(chatId, pId));
        }
        if (StringUtils.isNotBlank(userId)) {
            userPService.saveOrUpdateByMultiId(new UserP(userId, pId));
        }
        if (StringUtils.isNotBlank(cProjectId)) {
            LambdaQueryWrapper<Coverity> lq = Wrappers.lambdaQuery();
            lq.eq(Coverity::getCProjectId, cProjectId);
            lq.orderByDesc(Coverity::getUpdateTime);
            List<Coverity> list = coverityService.list(lq);
            LambdaUpdateWrapper<Coverity> lu = Wrappers.lambdaUpdate();
            lu.eq(Coverity::getCProjectId, cProjectId);
            lu.set(Coverity::getIsDelete, "1");
            coverityService.update(lu);
            Coverity coverity = list.get(0);
            coverity.setIsDelete("0");
            coverity.setRProjectId(pId);
            coverity.setId(null);
            coverityService.saveOrUpdate(coverity);
        }
        return Boolean.TRUE;
    }

    @GetMapping("/o")
    public ReturnT<Map<String, Object>> option() {
        Map<String, Object> map = new HashMap<>(4);
        LambdaQueryWrapper<GroupInfo> gp = Wrappers.lambdaQuery();
        gp.select(GroupInfo::getChatId, GroupInfo::getName);
        List<GroupInfo> groupInfoList = groupInfoService.list(gp);

        LambdaQueryWrapper<User> u = Wrappers.lambdaQuery();
        u.select(User::getId, User::getName);
        List<User> userList = loginService.list(u);

        LambdaQueryWrapper<Coverity> c = Wrappers.lambdaQuery();
        c.select(Coverity::getCProjectId, Coverity::getRProjectName);
        List<Coverity> coverityList = coverityService.list(c);
        map.put("group", groupInfoList);
        map.put("user", userList);
        map.put("coverity", coverityList);
        return new ReturnT<>(map);
    }
}
