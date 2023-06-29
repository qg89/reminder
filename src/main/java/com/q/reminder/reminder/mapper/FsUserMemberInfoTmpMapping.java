package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.FsUserMemberInfoTmp;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * redmine、飞书用户表关系表(FsUserMemberInfoTmp)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-29 10:52:06
 */
@Mapper
public interface FsUserMemberInfoTmpMapping extends BaseMapper<FsUserMemberInfoTmp> {
}

