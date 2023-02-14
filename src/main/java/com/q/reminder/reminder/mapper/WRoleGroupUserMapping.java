package com.q.reminder.reminder.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.q.reminder.reminder.entity.WRoleGroupUser;
import com.q.reminder.reminder.vo.GroupRoleUserIdsVo;
import com.q.reminder.reminder.vo.OptionVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * (WRoleGroupUser)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-28 10:04:07
 */
@Mapper
public interface WRoleGroupUserMapping extends MppBaseMapper<WRoleGroupUser> {
    List<GroupRoleUserIdsVo> option();
}

