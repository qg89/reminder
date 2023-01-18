package com.q.reminder.reminder.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.q.reminder.reminder.entity.TTableColumn;
import org.apache.ibatis.annotations.Mapper;

/**
 * (TTableColumn)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-18 14:15:49
 */
@Mapper
public interface TTableColumnMapping extends MppBaseMapper<TTableColumn> {
}

