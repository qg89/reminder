package com.q.reminder.reminder.strategys.config;

import com.q.reminder.reminder.enums.BizResultCode;
import com.q.reminder.reminder.enums.ReminderTypeEnum;
import com.q.reminder.reminder.exception.BizException;
import com.q.reminder.reminder.strategys.anno.RedmineTypeAnnotation;
import com.q.reminder.reminder.strategys.service.RedmineTypeStrategy;
import com.q.reminder.reminder.util.SpringContextUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.strategys.config.HandlerOrderContext
 * @Description :
 * @date :  2023.03.27 10:43
 */
@Log4j2
@Component
public abstract class HandlerTypeContext implements ApplicationContextAware {
    /**
     * 锁, 防止重复创建同一对象
     */
    private static final Object LOCK = new Object();

    /**
     * 创建redmine服务策略class集合 <key,value>=<redmine类型, 创建redmine服务策略class>
     * <p>
     * 注：此集合只存放RedmineTypeStrategy的子类class，对应的实例交由spring容器来管理
     */
    private static final Map<ReminderTypeEnum, Class<? extends RedmineTypeStrategy>> TYPE_ENUM_CLASS_HASH_MAP = new HashMap<>();

    /**
     * 获取创建redmine策略实例
     *
     * @param orderType redmine类型
     * @author chenck
     * @date 2020/2/20 9:53
     */
    public static RedmineTypeStrategy getInstance(Integer orderType) {
        if (null == orderType) {
            throw new BizException(BizResultCode.ERR_PARAM.getCode(), "redmine类型不能为空");
        }

        ReminderTypeEnum orderTypeEnum = ReminderTypeEnum.getEnum(orderType);
        if (null == orderTypeEnum) {
            throw new BizException(BizResultCode.ERR_PARAM.getCode(), "暂时不支持该redmine类型orderType=" + orderType);
        }

        // 当集合为空时，则初始化
        if (TYPE_ENUM_CLASS_HASH_MAP.isEmpty()) {
            initStrategy();
        }

        Class<? extends RedmineTypeStrategy> clazz = TYPE_ENUM_CLASS_HASH_MAP.get(orderTypeEnum);
        if (null == clazz) {
            throw new BizException(BizResultCode.ERR_PARAM.getCode(), "未找到redmine类型(" + orderTypeEnum + ")的创建redmine策略实现类");
        }
        // 从spring容器中获取bean
        return SpringContextUtils.getBean(clazz);
    }

    /**
     * 初始化
     */
    private static void initStrategy() {
        synchronized (LOCK) {
            // 获取接口下所有实例bean
            Map<String, RedmineTypeStrategy> strategyMap = SpringContextUtils.getBeanOfType(RedmineTypeStrategy.class);
            if (CollectionUtils.isEmpty(strategyMap)) {
                throw new BizException(BizResultCode.ERR_SYSTEM.getCode(), "代码配置错误：未获取到RedmineTypeStrategy的实现类，请检查代码中是否有将实现类bean注册到spring容器");
            }

            // 加载所有策略类对应的配置
            RedmineTypeAnnotation annotation;
            for (Map.Entry<String, RedmineTypeStrategy> strategy : strategyMap.entrySet()) {
                Class<? extends RedmineTypeStrategy> strategyClazz = strategy.getValue().getClass();
                // 因为策略bean可能是经过动态代理生成的bean实例（可能是多重动态代理后的代理对象），
                // 故而bean实例的class可能已经不是原来的class了，所以beanClass.getAnnotation(...)获取不到对应的注解元信息
                annotation = strategyClazz.getAnnotation(RedmineTypeAnnotation.class);
                if (null == annotation) {
                    // 当从bean实例的class上获取不到注解元信息时，通过AnnotationUtils工具类递归来获取
                    annotation = AnnotationUtils.findAnnotation(strategyClazz, RedmineTypeAnnotation.class);
                    if (null == annotation) {
                        log.warn("代码配置错误：创建redmine策略实现类{}未配置OrderTypeAnnotation注解", strategyClazz.getName());
                        continue;
                    }
                }
                // 支持多个事件类型
                ReminderTypeEnum typeEnum = annotation.type();
                //String key = getKey(typeEnum.getOrderType());
                if (TYPE_ENUM_CLASS_HASH_MAP.containsKey(typeEnum)) {
                    log.error("代码配置错误：一个redmine类型({})只能对应一个创建redmine策略实现{}", typeEnum, strategyClazz.getName());
                    throw new BizException(BizResultCode.ERR_SYSTEM.getCode(), "代码配置错误：一个redmine类型(" + typeEnum + ")只能对应一个创建redmine策略实现bean");
                }
                TYPE_ENUM_CLASS_HASH_MAP.put(typeEnum, strategyClazz);
            }

            if (TYPE_ENUM_CLASS_HASH_MAP.isEmpty()) {
                log.warn("初始化创建redmine策略集合失败");
            }
        }
    }
}
