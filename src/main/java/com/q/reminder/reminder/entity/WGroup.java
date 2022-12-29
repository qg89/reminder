package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * (WGroup)实体类
 *
 * @author makejava
 * @since 2022-12-28 10:35:12
 */
@Data
@AllArgsConstructor
@TableName("w_group")
public class WGroup extends Model<WGroup> implements Serializable {
    @Serial
    private static final long serialVersionUID = 280021198237880074L;
    
        
    @TableId(type = IdType.AUTO)
    private Integer id;
        
    private String group;


}

