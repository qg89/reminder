package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.WGroup;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (WGroup)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-28 10:35:14
 */
@Mapper
public interface WGroupMapping extends BaseMapper<WGroup> {

    List<RoleInvolvementVo> groupWorkload(@Param("vo") WorkloadParamsVo params);
}

