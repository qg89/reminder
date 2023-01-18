package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.jeffreyning.mybatisplus.anno.MppMultiId;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.baomidou.mybatisplus.extension.activerecord.Model;

import java.io.Serial;
import java.io.Serializable;


/**
 * (TTableColumn)实体类
 *
 * @author makejava
 * @since 2023-01-18 14:15:48
 */
@Data
@TableName("t_table_column")
public class TTableColumn extends Model<TTableColumn> implements Serializable {
    @Serial
    private static final long serialVersionUID = -89879487194351326L;

    /**
     * 多维数据表主键
     */
    @MppMultiId
    private Integer tableId;
    /**
     * 多维表列名称
     */
    @MppMultiId
    private String columnName;


}

