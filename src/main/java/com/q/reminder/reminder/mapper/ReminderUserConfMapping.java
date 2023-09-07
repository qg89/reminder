package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.ReminderUserConf;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.vo.UserReminderVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * redmine 设置不提醒写日报列表(ReminderUserConf)表数据库访问层
 *
 * @author makejava
 * @since 2023-09-06 16:52:34
 */
@Mapper
public interface ReminderUserConfMapping extends BaseMapper<ReminderUserConf> {
    List<UserReminderVo> listByUser(@Param("beginOfWeek") String beginOfWeek);
}

