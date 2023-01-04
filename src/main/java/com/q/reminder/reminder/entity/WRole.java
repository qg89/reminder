package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * (WRole)实体类
 *
 * @author makejava
 * @since 2022-12-28 10:19:37
 */
@Data
@AllArgsConstructor
@TableName("w_role")
public class WRole extends Model<WRole> implements Serializable {
    @Serial
    private static final long serialVersionUID = -55476342685759291L;
    
        
    @TableId(type = IdType.AUTO)
    private Integer id;
        
    private String role;

    private Integer sort;

}

