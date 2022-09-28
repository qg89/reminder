package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.AdminInfo
 * @Description :
 * @date :  2022.09.27 14:41
 */
@TableName("admin_info")
@Data
public class AdminInfo implements Serializable {

    private static final long serialVersionUID = -3444107822930134173L;
    @TableId(type = IdType.INPUT)
    private String memberId;
    @TableLogic(value = "0", delval = "1")
    private String isDelete;
}
