package com.q.reminder.reminder.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.extension.handlers.FastjsonTypeHandler;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 多维表格，表字段(TableFieldsChange)实体类
 *
 * @author makejava
 * @since 2023-07-13 15:19:10
 */
@Data
@TableName(value = "table_fields_change", autoResultMap = true)
public class TableFieldsChange extends Model<TableFieldsChange> implements Serializable {
    @Serial
    private static final long serialVersionUID = 253624788083114180L;
    
        
    @MppMultiId
    @TableField(value = "file_token")
    private String fileToken;
    /**
     * 多维表格数据表ID
     */    
    @MppMultiId
    @TableField(value = "table_id")
    private String tableId;
    /**
     * 字段 ID
     */    
    @MppMultiId
    @TableField(value = "field_id")
    private String fieldId;
    /**
     * 字段名字
     */    
    @TableField(value = "field_name")
    private String fieldName;
        
    @TableField(value = "property", typeHandler = FastjsonTypeHandler.class)
    private JSONObject property;
    /**
     * 字段类型
     */    
    @TableField(value = "type")
    private Integer type;
        
    @TableLogic(value = "0", delval = "1")
    @TableField(value = "is_delete")
    private String isDelete;
        
    @TableField(value = "update_time")
    private Date updateTime;
        
    @TableField(value = "create_time")
    private Date createTime;


}

