package com.q.reminder.reminder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.q.reminder.reminder.vo.FeautreTimeVo;
import com.q.reminder.reminder.vo.RedmineDataVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 需求管理表临时表（避免重复）(TTableFeatureTmp)表数据库访问层
 *
 * @author makejava
 * @since 2023-02-01 17:36:49
 */
@Mapper
public interface TTableFeatureTmpMapping extends BaseMapper<TTableFeatureTmp> {
    List<FeautreTimeVo> queryAllTimes();

    List<RedmineDataVo> listByProject();
}

