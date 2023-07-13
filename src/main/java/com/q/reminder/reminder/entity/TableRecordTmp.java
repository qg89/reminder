package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;


/**
 * 多维表格变更记录表(TableRecordTmp)实体类
 *
 * @author makejava
 * @since 2023-07-13 14:28:34
 */
@Data
@TableName("table_record_tmp")
public class TableRecordTmp extends Model<TableRecordTmp> implements Serializable {
    @Serial
    private static final long serialVersionUID = 989888555224111581L;
    
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
    /**
     * 发生变更的字段 ID
     */    
    @MppMultiId
    @TableField(value = "field_id")
    private String fieldId;
    /**
     * 文件类型
     */    
    @TableField(value = "file_type")
    private String fileType;
    /**
     * 发生变更的字段名称
     */    
    @TableField(value = "field_value")
    private String fieldValue;
        
    @TableField(value = "update_time")
    private Date updateTime;
        
    @TableField(value = "create_time")
    private Date createTime;


}

