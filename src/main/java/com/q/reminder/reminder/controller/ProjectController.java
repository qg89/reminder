package com.q.reminder.reminder.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.poi.excel.BigExcelWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.q.reminder.reminder.cpp.FeiShuToken;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RdTimeEntryService;
import com.q.reminder.reminder.vo.*;
import com.q.reminder.reminder.vo.base.ReturnT;
import com.q.reminder.reminder.vo.params.ProjectParamsVo;
import com.q.reminder.reminder.vo.params.UserInfoParamsVo;
import com.taskadapter.redmineapi.RedmineException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.ProjectController
 * @Description :
 * @date :  2022.12.21 19:13
 */
@Log4j2
@RestController
@RequestMapping("/p")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectInfoService projectInfoService;
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
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        String fileName = list.get(0).getShortName() + "-" + DateUtil.today();
        response.setHeader("Content-Disposition","attachment;filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8) + ".xlsx");
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



    /**
     * 用户管理
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/userInfoList")
    public ReturnT<IPage<UserInfoWrokVo>> userInfoList(Page<UserInfoWrokVo> page, UserInfoParamsVo vo) {
        IPage<UserInfoWrokVo> list = rdTimeEntryService.userinfoList(page, vo);
        return new ReturnT<>(list);
    }

    /**
     * 用户详情
     * @param page
     * @param vo
     * @return
     */
    @GetMapping("/userInfo")
    public ReturnT<IPage<UserInfoTimeVo>> userInfo(Page<UserInfoTimeVo> page, UserInfoParamsVo vo) {
        return new ReturnT<>(rdTimeEntryService.userTimeList(page, vo));
    }

    /**
     * 在职人员下拉列表
     * @return
     */
    @GetMapping("/userOption")
    public ReturnT<List<OptionVo>> userOption() {
        List<OptionVo> optionVos = rdTimeEntryService.userOption();
        return new ReturnT<>(optionVos);
    }

    @GetMapping("/so")
    public ReturnT<String> so() {
        log.info("init - system loadLibary");
        System.loadLibrary("Demo123");
        log.info("init - so success");
        FeiShuToken feiShuToken = new FeiShuToken();
        String i = feiShuToken.getToken();
        return new ReturnT<>(i);
    }

    @GetMapping("/so/{token}")
    public ReturnT<String> so1(@PathVariable("token") String token) {
        log.info("init - system loadLibary");
        System.load("/usr/java/openjdk-17/include/linux/"+ token);
        log.info("init - so success");
        FeiShuToken feiShuToken = new FeiShuToken();
        String i = feiShuToken.getToken();
        return new ReturnT<>(i);
    }
}
