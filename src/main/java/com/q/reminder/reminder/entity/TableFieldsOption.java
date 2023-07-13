package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;



/**
 * (TableFieldsOption)实体类
 *
 * @author makejava
 * @since 2023-07-13 16:51:16
 */
@Data
@TableName("table_fields_option")
public class TableFieldsOption extends Model<TableFieldsOption> implements Serializable {
    @Serial
    private static final long serialVersionUID = -49494672467996272L;
    
        
    @TableId(type = IdType.AUTO)
    @TableField(value = "id")
    private String id;
    

         
    @TableField(value = "name")
    private String name;
     
         
    @TableField(value = "color")
    private Integer color;
     
}

