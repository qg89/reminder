package com.q.reminder.reminder.entity;

import java.util.Date;
import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;



/**
 * (TableRecordTmp)实体类
 *
 * @author makejava
 * @since 2023-07-13 14:15:15
 */
@Data
@TableName("table_record_tmp")
public class TableRecordTmp extends Model<TableRecordTmp> implements Serializable {
    @Serial
    private static final long serialVersionUID = 851244939756249010L;
    
    /**
     * 主键
     */    
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private Integer id;
    

    /**
     * 发生变更的数据表 ID
     */     
    @TableField(value = "table_id")
    private String tableId;
     
    /**
     * 文件类型
     */     
    @TableField(value = "file_type")
    private String fileType;
     
    /**
     * 记录 ID
     */     
    @TableField(value = "record_id")
    private String recordId;
     
    /**
     * 发生变更的字段 ID
     */     
    @TableField(value = "field_id")
    private String fieldId;
     
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

