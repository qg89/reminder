package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.TableFeatureRole;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 需求管理表各角色列表(TableFeatureRole)表数据库访问层
 *
 * @author makejava
 * @since 2023-10-24 12:25:00
 */
@Mapper
public interface TableFeatureRoleMapping extends MppBaseMapper<TableFeatureRole> {
}

