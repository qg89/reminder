package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.ExcelVo
 * @Description :
 * @date :  2022.11.16 11:49
 */
@Data
public class ExcelVo implements Serializable {

    private String subject;
    private String weekNum;
    private String status;
    private String assignee;
    private String description;
    private String createTime;
    private String endTime;

    @Data
    public static class ExcelTimeVo {
        private String weekNum;
        private Double all;
        private Double bug;
        private Double all81;
        private Double bug81;
    }
}
