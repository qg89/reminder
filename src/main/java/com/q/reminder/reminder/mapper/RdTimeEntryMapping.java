package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.vo.*;
import com.q.reminder.reminder.vo.params.ProjectParamsVo;
import com.q.reminder.reminder.vo.params.UserInfoParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (RdTimeEntry)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-19 11:47:41
 */
@Mapper
public interface RdTimeEntryMapping extends MppBaseMapper<RdTimeEntry> {
    List<OvertimeVo> listOvertime(@Param("vo") ProjectParamsVo vo);

    IPage<UserInfoWrokVo> userinfoList(@Param("page") Page<UserInfoWrokVo> page, @Param("vo") UserInfoParamsVo vo);

    IPage<UserInfoTimeVo> userTimeList(@Param("page") Page<UserInfoTimeVo> page,@Param("vo") UserInfoParamsVo vo);

    List<OptionVo> userOption();

    List<RdTimeEntry> listByProject(@Param("vo") ProjectParamsVo vo);

    List<ProjectCostVo> listByProjectByDate(@Param("vo") ProjectParamsVo param);
    List<ProjectCostVo> listBySpentOnToCost(@Param("vo") ProjectParamsVo param);

    List<ProjectUserCostVo> listByPidSpentOnToCost(@Param("vo") ProjectParamsVo vo);
}

