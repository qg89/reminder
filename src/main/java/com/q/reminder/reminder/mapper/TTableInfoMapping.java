package com.q.reminder.reminder.mapper;

import com.q.reminder.reminder.entity.TTableInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.q.reminder.reminder.vo.table.FeatureVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * (TTableInfo)表数据库访问层
 *
 * @author makejava
 * @since 2023-01-18 13:31:18
 */
@Mapper
public interface TTableInfoMapping extends BaseMapper<TTableInfo> {
    List<FeatureVo> listByTableType(@Param("featureTmp") String featureTmp);
}

