package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.WConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * (WConfig)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-28 08:55:22
 */
@Mapper
public interface WConfigMapping extends BaseMapper<WConfig> {
}

