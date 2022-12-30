package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.WRole;
import com.q.reminder.reminder.vo.RoleInvolvementVo;
import com.q.reminder.reminder.vo.WorkloadParamsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色(WRole)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-29 09:16:16
 */
@Mapper
public interface WRoleMapping extends BaseMapper<WRole> {
    List<RoleInvolvementVo> roleInvolvement(@Param("vo") WorkloadParamsVo params);
}

