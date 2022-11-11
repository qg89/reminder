package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.FeacherListVo
 * @Description :
 * @date :  2022.10.26 13:35
 */
@Data
public class FeatureListVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 7892264413369571086L;
    /**
     * 该单元格sheetId!A1:B2
     */
    private String range;
    /**
     * rfq ID
     */
    private String rfqId;
    /**
     * 需求ID
     */
    private String featureId;
    /**
     * 是否等级redmine
     */
    private String isRedmine;
    /**
     * 模块
     */
    private String module;
    /**
     * 里程碑
     */
    private String milestone;
    /**
     * 一级菜单
     */
    private String menuOne;
    /**
     * 二级菜单
     */
    private String menuTwo;
    /**
     * 三级菜单
     */
    private String menuThree;
    /**
     * 功能描述
     */
    private String desc;
    /**
     * 优先级
     */
    private String priority;
    /**
     * 需求类型
     */
    private String featureType;
    /**
     * 来源
     */
    private String featureSource;
    /**
     * 状态
     */
    private String featureStatus;
    /**
     * 产品经理
     */
    private String product;
    /**
     * 目标版本
     */
    private String target;
    /**
     * 转测时间
     */
    private String testTime;
    /**
     * redmineId
     */
    private String redmineId;
    /**
     * 关联需求父ID
     */
    private String parentFeatureId;
    /**
     * 前端
     */
    private String front;
    /**
     * 后端
     */
    private String backend;
    /**
     * 大数据
     */
    private String bigData;
    /**
     * 算法
     */
    private String algorithm;
    /**
     * 测试
     */
    private String test;
    /**
     * 生产发布时间
     */
    private String prodTime;
    /**
     * 生产发布时间
     */
    private String devTime;

    /**
     * 需求管理不体现此字段
     */
    private String redmineSubject;
}
