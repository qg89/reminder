package com.q.reminder.reminder.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.q.reminder.reminder.entity.*;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.vo.*;
import com.q.reminder.reminder.vo.base.ReturnT;
import com.q.reminder.reminder.vo.params.ProjectParamsVo;
import com.q.reminder.reminder.vo.params.UserInfoParamsVo;
import com.taskadapter.redmineapi.RedmineException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    private final GroupInfoService groupInfoService;
    private final LoginService loginService;
    private final UserMemberService userMemberService;
    private final RdTimeEntryService rdTimeEntryService;


    @GetMapping("/list")
    public ReturnT<Page<RProjectInfo>> listProject(Page<RProjectInfo> page) {
        return new ReturnT<>(projectInfoService.page(page));
    }

    @GetMapping("/cost")
    public ReturnT<List<ProjectCostVo>> projectCost(ProjectParamsVo vo) throws RedmineException {
        List<ProjectCostVo> list = projectInfoService.projectCost(vo);
        return new ReturnT<>(list);
    }

    @SneakyThrows
    @GetMapping("/exportCost")
    public void exportCostByPid(ProjectParamsVo vo, HttpServletResponse response) {
        List<ProjectUserCostVo> list = projectInfoService.exportCostByPid(vo);
        BigExcelWriter writer = new BigExcelWriter();
        writer.setSheet(0);
        Map<String, String> header = new LinkedHashMap<>();
        header.put("userName", "员工姓名");
        header.put("spentOn", "填写日期");
        header.put("peopleHours", "实际工时（小时）");
        header.put("overtime", "加班工时（小时）");
        header.put("normal", "项目内正常工时（小时）");
        writer.setHeaderAlias(header);
        writer.setOnlyAlias(true);
        writer.setFreezePane(1);
        writer.autoSizeColumnAll();
        writer.renameSheet("日报明细");
        writer.write(list, true);
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        String fileName = list.get(0).getShortName() + "-" + DateUtil.today() + ".xlsx";
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8));
        ServletOutputStream outputStream = null;
        try {
            outputStream = response.getOutputStream();
            writer.flush(outputStream, true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            writer.close();
            IoUtil.close(outputStream);
        }
    }

    @GetMapping("/d/{id}")
    public ReturnT<Boolean> d(@PathVariable("id") String id) {
        return new ReturnT<>(projectInfoService.removeById(id));
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

    @GetMapping("/userInfoList")
    public ReturnT<IPage<UserInfoWrokVo>> userInfoList(Page<UserInfoWrokVo> page, UserInfoParamsVo vo) {
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
