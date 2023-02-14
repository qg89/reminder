package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.DefinitionVo
 * @Description :
 * @date :  2022.10.27 13:23
 */
@Data
public class DefinitionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 3180616623542952311L;
    /**
     * 产品
     */
    private String product;
    /**
     * 前端
     */
    private String front;
    /**
     * 后端
     */
    private String backend;
    /**
     * 大数据
     */
    private String bigData;
    /**
     * 算法
     */
    private String algorithm;
    /**
     * 测试
     */
    private String test;

    private String redmineUrl;
    private String apiAccessKey;
    private Integer projectId;
    private String redmineType;
}
