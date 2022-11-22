package com.q.reminder.reminder.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.impl.WeeklyServiceImpl;
import com.q.reminder.reminder.task.WeeklyProjectMonReportTask;
import com.q.reminder.reminder.vo.WeeklyByProjectVo;
import com.q.reminder.reminder.vo.WeeklyProjectVo;
import com.q.reminder.reminder.vo.WeeklyVo;
import com.xxl.job.core.biz.model.ReturnT;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.WeeklyProjectReportController
 * @Description :
 * @date :  2022.11.15 11:30
 */
@RestController
@RequestMapping("/weekly")
public class WeeklyProjectReportController {

    @Autowired
    private WeeklyProjectMonReportTask weeklyProjectMonReportTask;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private WeeklyServiceImpl weeklyService;

    /**
     * 重新生成
     */
    @GetMapping("/reset")
    public ReturnT<String> reSet(WeeklyVo vo) {
        ReturnT<String> returnT = new ReturnT<>();
        Integer weekNum = vo.getWeekNum();
        List<WeeklyProjectVo> list = projectInfoService.getWeeklyDocxList(weekNum, vo.getPKey());
        WeeklyProjectVo projectVo = list.get(0);
        if (projectVo == null) {
            return ReturnT.FAIL;
        }
        CopyOptions copyOptions = CopyOptions.create()
                .setIgnoreNullValue(true)
                .setIgnoreCase(true);
        BeanUtil.copyProperties(projectVo, vo, copyOptions);
        try {
            weeklyService.resetReport(vo);
        } catch (Exception e) {
            returnT.setMsg(e.getMessage());
            returnT.setCode(500);
            return returnT;
        }
        returnT.setContent(projectVo.getWeeklyReportUrl());
        return returnT;
    }

    @GetMapping("/p_option")
    public ReturnT<List<ProjectInfo>> option() {
        LambdaQueryWrapper<ProjectInfo> lq = Wrappers.<ProjectInfo>lambdaQuery().select(ProjectInfo::getPKey, ProjectInfo::getProjectShortName);
        return new ReturnT<>(projectInfoService.list(lq));
    }

    @GetMapping("/listReport")
    public ReturnT<List<WeeklyByProjectVo>> listReport(WeeklyByProjectVo vo) {
       return new ReturnT<>(projectInfoService.weeklyByProjectList(vo.getPKey(), vo.getFileName()));
    }

    @GetMapping("/listDocx")
    public ReturnT<List<String>> list() {
        List<String> list = List.of(
                "评审问题数量",
                "趋势",
                "Open Bug情况（未实现）",
                "All-Bug等级分布",
                "Open-Bug等级分布",
                "Open-Bug>15",
                "COPQ（Cost Of Poor Quality）"
        );
        return new ReturnT<>(list);
    }
}
