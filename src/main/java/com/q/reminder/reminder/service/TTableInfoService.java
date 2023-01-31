package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.TTableInfo;
import com.q.reminder.reminder.vo.table.FeatureVo;

import java.util.List;


/**
 * (TTableInfo)表服务接口
 *
 * @author makejava
 * @since 2023-01-18 13:31:18
 */
public interface TTableInfoService extends IService<TTableInfo>{

    List<FeatureVo> listByTableType(String featureTmp);
}
