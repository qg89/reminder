package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 需求管理表-人员配置表（按项目）(TTableUserConfig)实体类
 *
 * @author makejava
 * @since 2023-01-31 17:37:06
 */
@Data
@TableName("t_table_user_config")
public class TTableUserConfig extends Model<TTableUserConfig> implements Serializable {
    @Serial
    private static final long serialVersionUID = -48484223040390163L;

    /**
     * 项目表主键
     */
    @TableId(type = IdType.AUTO)
    @TableField("p_id")
    private Long pId;
    /**
     * 项目名称
     */
    @TableField(value = "prdct_name", fill = FieldFill.INSERT_UPDATE)
    private String prdctName;
    /**
     * 产品redmine人员ID
     */
    @TableField(value = "prdct_id", fill = FieldFill.INSERT_UPDATE)
    private Integer prdctId;
    /**
     * 前端redmine人员ID
     */
    @TableField(value = "front_id", fill = FieldFill.INSERT_UPDATE)
    private Integer frontId;
    /**
     * 后端redmine人员ID
     */
    @TableField(value = "back_id", fill = FieldFill.INSERT_UPDATE)
    private Integer backId;
    /**
     * 大数据redmine人员ID
     */
    @TableField(value = "bgdt_id", fill = FieldFill.INSERT_UPDATE)
    private Integer bgdtId;
    /**
     * 测试redmine人员ID
     */
    @TableField(value = "test_id", fill = FieldFill.INSERT_UPDATE)
    private Integer testId;
    /**
     * 算法redmine人员ID
     */
    @TableField(value = "algrthm_id", fill = FieldFill.INSERT_UPDATE)
    private Integer algrthmId;
    /**
     * 架构redmine人员ID
     */
    @TableField(value = "archtct_id", fill = FieldFill.INSERT_UPDATE)
    private Integer archtctId;
    /**
     * 运维redmine人员ID
     */
    @TableField(value = "oprton_id", fill = FieldFill.INSERT_UPDATE)
    private Integer oprtonId;
    /**
     * 实施redmine人员ID
     */
    @TableField(value = "implmntton_id", fill = FieldFill.INSERT_UPDATE)
    private Integer implmnttonId;


}

