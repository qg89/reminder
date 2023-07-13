package com.q.reminder.reminder.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 多维表格，表字段(TableFieldsFeature)实体类
 *
 * @author makejava
 * @since 2023-07-13 14:02:09
 */
@Data
@TableName("table_fields_feature")
public class TableFieldsFeature extends Model<TableFieldsFeature> implements Serializable {
    @Serial
    private static final long serialVersionUID = -58493307939167717L;

    /**
     * 字段 ID
     */
    @TableId(type = IdType.AUTO)
    @TableField(value = "field_id")
    private String fieldId;

    /**
     * 字段名字
     */
    @TableField(value = "field_name")
    private String fieldName;


    @TableField(value = "is_primary")
    private String isPrimary;


    @TableField(value = "property")
    private JSONObject property;

    /**
     * 字段类型
     */
    @TableField(value = "type")
    private Integer type;


    @TableField(value = "ui_type")
    private String uiType;

    /**
     * 多维表格数据表ID
     */
    @TableField(value = "table_id")
    private String tableId;


    @TableLogic(value = "0", delval = "1")
    @TableField(value = "is_delete")
    private String isDelete;


    @TableField(value = "update_time")
    private Date updateTime;


    @TableField(value = "create_time")
    private Date createTime;

}

