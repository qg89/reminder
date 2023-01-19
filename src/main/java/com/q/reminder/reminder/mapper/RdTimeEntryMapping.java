package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.RdTimeEntry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * (RdTimeEntry)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-19 11:29:00
 */
@Mapper
public interface RdTimeEntryMapping extends BaseMapper<RdTimeEntry> {
}

