package com.q.reminder.reminder.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.ProjectInfo
 * @Description :
 * @date :  2022.09.27 13:56
 */
@Data
@TableName("project_info")
public class ProjectInfo implements Serializable {

    private static final long serialVersionUID = 6718764047088702656L;
    private String pId;
    private String pKey;
    private String pName;
}
