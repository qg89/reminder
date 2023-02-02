package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 需求管理表临时表（避免重复）(TTableFeatureTmp)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-01 17:36:49
 */
@Mapper
public interface TTableFeatureTmpMapping extends BaseMapper<TTableFeatureTmp> {
}

