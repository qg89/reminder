package com.q.reminder.reminder.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * (WRole)实体类
 *
 * @author makejava
 * @since 2022-12-28 10:19:37
 */
@Data
@TableName("w_role")
public class WRole extends Model<WRole> implements Serializable {
    @Serial
    private static final long serialVersionUID = -55476342685759291L;
    
        
    @TableId(type = IdType.AUTO)
    private Integer id;

    @JSONField(name = "role")
    private String role;

    @JSONField(name = "sort")
    private Integer sort;

}

