package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * (TTableInfo)实体类
 *
 * @author makejava
 * @since 2023-01-18 13:38:08
 */
@Data
@TableName("t_table_info")
public class TTableInfo extends Model<TTableInfo> implements Serializable {
    @Serial
    private static final long serialVersionUID = -98745725932983735L;
    
        
    @TableId(type = IdType.AUTO)
    private Integer id;
        
    private String appToken;
        
    private String tableId;
        
    private String viewId;
    /**
     * 多维表格类型：1项目工时，2需求管理表，3需求管理表临时表
     */    
    private String tableType;
    /**
     * 多维表格数据表视图类型
     */    
    private String viewType;

    private String filter;


}

