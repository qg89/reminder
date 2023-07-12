package com.q.reminder.reminder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.q.reminder.reminder.constant.RedisKeyContents;
import com.q.reminder.reminder.entity.TTableUserConfig;
import com.q.reminder.reminder.mapper.TTableUserConfigMapping;
import com.q.reminder.reminder.service.TTableUserConfigService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 需求管理表-人员配置表（按项目）(TTableUserConfig)表服务实现类
 *
 * @author makejava
 * @since 2023-01-31 17:37:06
 */
@Service
public class TTableUserConfigServiceImpl extends ServiceImpl<TTableUserConfigMapping, TTableUserConfig> implements TTableUserConfigService {

    @Override
    @Cacheable(cacheNames = RedisKeyContents.TABLE_USER_CONFIG, key = "'userConfig'", unless = "#result == null")
    public List<TTableUserConfig> listAll() {
        return list();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyContents.TABLE_USER_CONFIG, allEntries = true)
    public void saveInfo(TTableUserConfig entity) {
        saveOrUpdate(entity);
    }
}
