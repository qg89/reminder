package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.SDateConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日期表(SDateConfig)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-28 10:04:07
 */
@Mapper
public interface SDateConfigMapping extends BaseMapper<SDateConfig> {
}

