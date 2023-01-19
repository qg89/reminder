package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * (TTableUserTime)实体类
 *
 * @author makejava
 * @since 2023-01-18 18:33:05
 */
@Data
@TableName("t_table_user_time")
public class TTableUserTime extends Model<TTableUserTime> implements Serializable {
    @Serial
    private static final long serialVersionUID = -53430442482907394L;


    @MppMultiId
    private String columnEntity;

    @MppMultiId
    private String tableColumnName;
    /**
     * 多维表主键
     */
    private Integer tableId;


}

