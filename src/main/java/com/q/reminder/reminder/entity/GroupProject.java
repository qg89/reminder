package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.GroupProject
 * @Description :
 * @date :  2022.12.21 19:22
 */
@TableName("group_project")
@Data
@AllArgsConstructor
public class GroupProject implements Serializable {

    @Serial
    private static final long serialVersionUID = -6214772315057217650L;
    private String chatId;
    private String pId;
}
