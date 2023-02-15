package com.q.reminder.reminder.vo.table;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.table.FeatureUserConfigVo
 * @Description :
 * @date :  2023.02.02 11:22
 */
@Data
public class FeatureUserConfigVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -3247673297296429346L;

    private String recordsId;
    private String prjctKey;
    private String prjct;
    private String prdct;
    private String front;
    private String back;
    private String bgdt;
    private String test;
    private String algrthm;
    private String archtct;
    private String oprton;
    private String implmntton;

}
