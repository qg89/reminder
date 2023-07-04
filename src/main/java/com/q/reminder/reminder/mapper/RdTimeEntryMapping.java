package com.q.reminder.reminder.mapper;

import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import com.q.reminder.reminder.entity.RdTimeEntry;
import com.q.reminder.reminder.vo.OvertimeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (RdTimeEntry)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-19 11:47:41
 */
@Mapper
public interface RdTimeEntryMapping extends MppBaseMapper<RdTimeEntry> {
    List<OvertimeVo> listOvertime(@Param("ym") String ym);
}

