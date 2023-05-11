package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.RdIssueBug;
import com.github.jeffreyning.mybatisplus.base.MppBaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * redmine问题记录表(RdIssueBug)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-11 18:18:54
 */
@Mapper
public interface RdIssueBugMapping extends MppBaseMapper<RdIssueBug> {
}

