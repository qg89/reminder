package com.q.reminder.reminder.vo;

import com.q.reminder.reminder.entity.TTableFeatureTmp;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.RedmineDataVo
 * @Description :
 * @date :  2023.03.27 11:29
 */
@Data
public class RedmineDataVo extends TTableFeatureTmp implements Serializable {

    @Serial
    private static final long serialVersionUID = -6047970696543137189L;


    private String redmineType;
    private String pmKey;

    private String redmineUrl;
    private Integer pId;
    private Integer prdctId;

}
