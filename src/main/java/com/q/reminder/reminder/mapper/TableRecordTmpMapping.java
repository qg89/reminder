package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.TableRecordTmp;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 多维表格变更记录表(TableRecordTmp)表数据库访问层
 *
 * @author makejava
 * @since 2023-07-13 14:28:37
 */
@Mapper
public interface TableRecordTmpMapping extends MppBaseMapper<TableRecordTmp> {
}

