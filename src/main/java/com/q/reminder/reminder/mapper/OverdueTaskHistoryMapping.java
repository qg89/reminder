package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.OverdueTaskHistory;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.mapper.OverdueTaskHistoryMapping
 * @Description :
 * @date :  2022.09.27 14:33
 */
@Mapper
public interface OverdueTaskHistoryMapping extends BaseMapper<OverdueTaskHistory> {
}
