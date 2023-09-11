package com.q.reminder.reminder.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 多维表格变更记录表(TableRecordTmp)实体类
 *
 * @author makejava
 * @since 2023-09-11 18:54:40
 */
@Data
@TableName("table_record_tmp")
public class TableRecordTmp extends Model<TableRecordTmp> implements Serializable {
    @Serial
    private static final long serialVersionUID = 826887830194135167L;
    
/**
     * 发生变更的数据表 ID
     */    
    @MppMultiId
    @TableField(value = "table_id")
    private String tableId;
/**
     * 记录 ID
     */    
    @MppMultiId
    @TableField(value = "record_id")
    private String recordId;
    
    @MppMultiId
    @TableField(value = "field_id")
    private String fieldId;
/**
     * 文件类型
     */    
    @TableField(value = "file_type")
    private String fileType;
    
    @TableField(value = "before_field_value")
    private String beforeFieldValue;
/**
     * 发生变更的字段名称
     */    
    @TableField(value = "after_field_value")
    private String afterFieldValue;
    
    @TableField(value = "update_time")
    private Date updateTime;
    
    @TableField(value = "create_time")
    private Date createTime;


}

