package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.WUserTimes;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.vo.ProjectUserTimeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * (WUserTimes)表数据库访问层
 *
 * @author makejava
 * @since 2022-12-28 11:32:44
 */
@Mapper
public interface WUserTimesMapping extends BaseMapper<WUserTimes> {
    List<Map<String, String>> listByTable(@Param("day") String day, @Param("dayType") String dayType);
}

