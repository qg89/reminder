package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.TTableUserConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 需求管理表-人员配置表（按项目）(TTableUserConfig)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-31 17:37:06
 */
@Mapper
public interface TTableUserConfigMapping extends BaseMapper<TTableUserConfig> {
}

