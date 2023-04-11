package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * (TableFieldsFeature)实体类
 *
 * @author makejava
 * @since 2023-04-11 15:58:43
 */
@Data
@TableName("table_fields_feature")
public class TableFieldsFeature extends Model<TableFieldsFeature> implements Serializable {
    @Serial
    private static final long serialVersionUID = -82978425891225406L;


    @TableId(type = IdType.INPUT)
    @TableField(value = "field_id")
    private String fieldId;

    @TableField(value = "field_name")
    private String fieldName;

    @TableField(value = "is_primary")
    private String isPrimary;

    @TableField(value = "property")
    private String property;

    @TableField(value = "type")
    private Integer type;

    @TableField(value = "ui_type")
    private String uiType;


    @TableField(value = "table_id")
    private String tableId;

    @TableLogic(value = "0", delval = "1")
    @TableField(value = "is_delete")
    private String isDelete;
}

