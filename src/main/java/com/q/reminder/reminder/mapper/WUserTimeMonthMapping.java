package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.WUserTimeMonth;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (WUserTimeMonth)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-28 10:32:53
 */
@Mapper
public interface WUserTimeMonthMapping extends BaseMapper<WUserTimeMonth> {

    List<RoleInvolvementVo> inputRatio(@Param("vo") WorkloadParamsVo params);
}

