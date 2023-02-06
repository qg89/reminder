package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 需求管理表临时表（避免重复）(TTableFeatureTmp)实体类
 *
 * @author makejava
 * @since 2023-02-02 10:22:29
 */
@Data
@TableName("t_table_feature_tmp")
public class TTableFeatureTmp extends Model<TTableFeatureTmp> implements Serializable {
    @Serial
    private static final long serialVersionUID = -71120729372707351L;

    /**
     * 记录ID
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "records_id")
    private String recordsId;
    /**
     * 项目
     */
    @TableField(value = "prjct", fill = FieldFill.INSERT_UPDATE)
    private String prjct;
    /**
     * 模块
     */
    @TableField(value = "mdl", fill = FieldFill.INSERT_UPDATE)
    private String mdl;
    /**
     * 一级
     */
    @TableField(value = "menu_one", fill = FieldFill.INSERT_UPDATE)
    private String menuOne;
    /**
     * 二级
     */
    @TableField(value = "menu_two", fill = FieldFill.INSERT_UPDATE)
    private String menuTwo;
    /**
     * 三级
     */
    @TableField(value = "menu_three", fill = FieldFill.INSERT_UPDATE)
    private String menuThree;
    /**
     * 三级
     */
    @TableField(value = "dscrptn", fill = FieldFill.INSERT_UPDATE)
    private String dscrptn;
    /**
     * 产品工时
     */
    @TableField(value = "prdct", fill = FieldFill.INSERT_UPDATE)
    private Float prdct;
    /**
     * 前端工时
     */
    @TableField(value = "front", fill = FieldFill.INSERT_UPDATE)
    private Float front;
    /**
     * 后端工时
     */
    @TableField(value = "back", fill = FieldFill.INSERT_UPDATE)
    private Float back;
    /**
     * 大数据工时
     */
    @TableField(value = "bgdt", fill = FieldFill.INSERT_UPDATE)
    private Float bgdt;
    /**
     * 实施工时
     */
    @TableField(value = "implmntton", fill = FieldFill.INSERT_UPDATE)
    private Float implmntton;
    /**
     * 架构工时
     */
    @TableField(value = "archtct", fill = FieldFill.INSERT_UPDATE)
    private Float archtct;
    /**
     * 测试工时
     */
    @TableField(value = "test", fill = FieldFill.INSERT_UPDATE)
    private Float test;
    /**
     * 生产发布-时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "prod_time", fill = FieldFill.INSERT_UPDATE)
    private Date prodTime;
    /**
     * 移动工时
     */
    @TableField(value = "andrd", fill = FieldFill.INSERT_UPDATE)
    private Float andrd;
    /**
     * 算法工时
     */
    @TableField(value = "algrthm", fill = FieldFill.INSERT_UPDATE)
    private Float algrthm;
    /**
     * 运维工时
     */
    @TableField(value = "oprton", fill = FieldFill.INSERT_UPDATE)
    private Float oprton;
    /**
     * 是否写入redmine：0未写入，1已写入完成
     */
    @TableField(value = "write_redmine")
    private String writeRedmine;

    @TableField(value = "update_time")
    private Date updateTime;

    @TableField(value = "create_time")
    private Date createTime;


}

