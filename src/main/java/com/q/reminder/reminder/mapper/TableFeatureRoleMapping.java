package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.TableFeatureRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 需求管理表各角色列表(TableFeatureRole)表数据库访问层
 *
 * @author makejava
 * @since 2023-10-24 11:26:16
 */
@Mapper
public interface TableFeatureRoleMapping extends BaseMapper<TableFeatureRole> {
}

