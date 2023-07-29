package com.q.reminder.reminder.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.q.reminder.reminder.entity.*;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.util.RedmineApi;
import com.q.reminder.reminder.vo.*;
import com.q.reminder.reminder.vo.base.ReturnT;
import com.q.reminder.reminder.vo.params.ProjectParamsVo;
import com.q.reminder.reminder.vo.params.UserInfoParamsVo;
import com.taskadapter.redmineapi.RedmineException;
import com.taskadapter.redmineapi.bean.Project;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.ProjectController
 * @Description :
 * @date :  2022.12.21 19:13
 */
@RestController
@RequestMapping("/p")
@RequiredArgsConstructor
public class ProjectController {
    
    private final ProjectInfoService projectInfoService;
    private final GroupProjectService groupProjectService;
    private final UserPService userPService;
    private final GroupInfoService groupInfoService;
    private final LoginService loginService;
    private final UserMemberService userMemberService;
    private final RdTimeEntryService rdTimeEntryService;


    @GetMapping("/list")
    public ReturnT<Page<RProjectInfo>> listProject(Page<RProjectInfo> page) {
        return new ReturnT<>(projectInfoService.page(page));
    }

    @GetMapping("/i")
    public ReturnT<List<List<ProjectInfoVo<?>>>> i(ProjectParamsVo vo) throws Exception {
        LambdaQueryWrapper<RProjectInfo> lq = Wrappers.lambdaQuery();
        lq.orderByDesc(RProjectInfo::getCreateTime);
        List<RProjectInfo> list = projectInfoService.list(lq);
        Map<String, String> userMap = userMemberService.list().stream().collect(Collectors.toMap(UserMemgerInfo::getMemberId, UserMemgerInfo::getName, (v1, v2) -> v1));
        Map<String, String> groupMap = groupInfoService.list().stream().collect(Collectors.toMap(FsGroupInfo::getChatId, FsGroupInfo::getName, (v1, v2) -> v1));
        Map<String, Double> projectMap = projectInfoService.getProjectCost();
        List<List<ProjectInfoVo<?>>> res = projectInfoService.listToArray(list, userMap, groupMap, projectMap, vo);
        return new ReturnT<>(res);
    }

    @GetMapping("/d/{id}")
    public ReturnT<Boolean> d(@PathVariable("id") String id) {
        return new ReturnT<>(projectInfoService.removeById(id));
    }

    @PostMapping("/s")
    public ReturnT<String> s(@RequestBody RProjectReaVo info) {
        try {
            Project project = RedmineApi.queryProjectByKey(info);
            if (project == null){
                return ReturnT.FAIL;
            }
            info.setPid(String.valueOf(project.getId()));
            String name = project.getName();
            info.setPname(name);
            info.setRedmineUrl(RedmineApi.REDMINE_PA_URL);
            if (Objects.equals("1", info.getRedmineType())) {
                info.setRedmineUrl(RedmineApi.REDMINE_URL);
            }
            projectInfoService.save(info);
            if (!saveRea(info)) {
                return ReturnT.FAIL;
            }
        } catch (RedmineException e) {
            throw new RuntimeException(e);
        }
        return ReturnT.SUCCESS;
    }

    @PostMapping("/e")
    public ReturnT<String> e(@RequestBody RProjectReaVo info) {
        if ("1".equals(info.getIsDelete())) {
            projectInfoService.removeById(info);
            return ReturnT.SUCCESS;
        }
        projectInfoService.updateInfo(info);
        if (!saveRea(info)) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @PostMapping("/r")
    public ReturnT<String> r(@RequestBody RProjectReaVo vo) {
        if (!saveRea(vo)) {
            return ReturnT.FAIL;
        }
        return ReturnT.SUCCESS;
    }

    @GetMapping("/info")
    public ReturnT<List<ProjectInfoVo>> info() {
        return new ReturnT<>(projectInfoService.listInfo());
    }

    private Boolean saveRea(RProjectReaVo vo) {
        String pId = vo.getPid();
        String chatId = vo.getChatId();
        String userId = vo.getUserId();
        if (StringUtils.isBlank(pId) || projectInfoService.getOne(Wrappers.<RProjectInfo>lambdaQuery().eq(RProjectInfo::getPid, pId)) == null) {
            return Boolean.FALSE;
        }
        if (StringUtils.isNotBlank(chatId)) {
            groupProjectService.saveOrUpdateByMultiId(new GroupProject(chatId, pId));
        }
        if (StringUtils.isNotBlank(userId)) {
            userPService.saveOrUpdateByMultiId(new UserP(userId, pId));
        }
        return Boolean.TRUE;
    }

    @GetMapping("/o")
    public ReturnT<Map<String, Object>> option() {
        Map<String, Object> map = new HashMap<>(4);
        LambdaQueryWrapper<FsGroupInfo> gp = Wrappers.lambdaQuery();
        gp.select(FsGroupInfo::getChatId, FsGroupInfo::getName);
        List<FsGroupInfo> fsGroupInfoList = groupInfoService.list(gp);
        LambdaQueryWrapper<User> u = Wrappers.lambdaQuery();
        u.select(User::getId, User::getName);
        List<User> userList = loginService.list(u);
        map.put("group", fsGroupInfoList);
        map.put("user", userList);
        return new ReturnT<>(map);
    }

    @GetMapping("/group")
    public ReturnT<List<OptionVo>> group() {
        LambdaQueryWrapper<FsGroupInfo> lq = Wrappers.lambdaQuery();
        lq.select(FsGroupInfo::getChatId, FsGroupInfo::getName);
        List<FsGroupInfo> list = groupInfoService.list(lq);
        List<OptionVo> res = new ArrayList<>();
        list.forEach(i -> {
            OptionVo vo = new OptionVo();
            vo.setId(i.getChatId());
            vo.setName(i.getName());
            res.add(vo);
        });
        return new ReturnT<>(res);
    }

    @GetMapping("/member")
    public ReturnT<List<OptionVo>> member() {
        LambdaQueryWrapper<UserMemgerInfo> lq = Wrappers.lambdaQuery();
        lq.select(UserMemgerInfo::getMemberId, UserMemgerInfo::getName);
        lq.eq(UserMemgerInfo::getResign, "0");
        List<OptionVo> res = new ArrayList<>();
        userMemberService.list(lq).forEach(i -> {
            OptionVo vo = new OptionVo();
            vo.setId(i.getMemberId());
            vo.setName(i.getName());
            res.add(vo);
        });
        return new ReturnT<>(res);
    }

    @GetMapping("/userInfoLit")
    public ReturnT<IPage<UserInfoWrokVo>> userInfoLit(Page<UserInfoWrokVo> page, UserInfoParamsVo vo) {
        IPage<UserInfoWrokVo> list = rdTimeEntryService.userinfoList(page, vo);
        return new ReturnT<>(list);
    }

    @GetMapping("/userInfo")
    public ReturnT<IPage<UserInfoTimeVo>> userInfo(Page<UserInfoTimeVo> page, UserInfoParamsVo vo) {
        return new ReturnT<>(rdTimeEntryService.userTimeList(page, vo));
    }

    @GetMapping("/userOption")
    public ReturnT<List<OptionVo>> userOption() {
        List<OptionVo> optionVos = rdTimeEntryService.userOption();
        return new ReturnT<>(optionVos);
    }
}
