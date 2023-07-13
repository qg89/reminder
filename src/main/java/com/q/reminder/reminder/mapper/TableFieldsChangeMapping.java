package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.TableFieldsChange;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 多维表格，表字段(TableFieldsChange)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-13 15:19:13
 */
@Mapper
public interface TableFieldsChangeMapping extends MppBaseMapper<TableFieldsChange> {
}

