package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.GitCommitLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * (GitCommitLog)表数据库访问层
 *
 * @author makejava
 * @since 2023-12-19 17:39:39
 */
@Mapper
public interface GitCommitLogMapping extends BaseMapper<GitCommitLog> {
}

