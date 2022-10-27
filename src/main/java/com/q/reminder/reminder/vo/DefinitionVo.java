package com.q.reminder.reminder.vo;

import lombok.Data;

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

    private static final long serialVersionUID = 3180616623542952311L;
    private String product;

    private String application;

    private String test;

    private String bigData;

    private String redmineUrl;
    private String apiAccessKey;
    private Integer projectId;
}
