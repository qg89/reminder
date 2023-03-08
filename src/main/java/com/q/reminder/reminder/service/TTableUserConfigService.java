package com.q.reminder.reminder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.q.reminder.reminder.entity.TTableUserConfig;

import java.util.List;


/**
 * 需求管理表-人员配置表（按项目）(TTableUserConfig)表服务接口
 *
 * @author makejava
 * @since 2023-01-31 17:37:06
 */
public interface TTableUserConfigService extends IService<TTableUserConfig>{

    List<TTableUserConfig> listAll();

    void saveInfo(TTableUserConfig entity);
}
