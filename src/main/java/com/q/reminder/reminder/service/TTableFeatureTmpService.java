package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.q.reminder.reminder.vo.FeautreTimeVo;
import com.q.reminder.reminder.vo.RedmineDataVo;

import java.util.List;


/**
 * 需求管理表临时表（避免重复）(TTableFeatureTmp)表服务接口
 *
 * @author makejava
 * @since 2023-02-01 17:36:49
 */
public interface TTableFeatureTmpService extends IService<TTableFeatureTmp> {

    List<FeautreTimeVo> queryAllTimes();

    List<RedmineDataVo> listByProject();

}
