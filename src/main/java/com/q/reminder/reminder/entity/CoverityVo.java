package com.q.reminder.reminder.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.entity.Coverity
 * @Description :
 * @date :  2022.10.09 09:15
 */
@Data
public class CoverityVo implements Serializable {

    private static final long serialVersionUID = 4135042126732197174L;
    private Integer cid;
    /**
     * 类别
     */
    private String displayCategory;
    /**
     * 类型
     */
    private String displayType;
    /**
     * 文件
     */
    private String displayFile;
    /**
     * 代码行数
     */
    private String lineNumber;
    /**
     * 组件
     */
    private String displayComponent;
}
