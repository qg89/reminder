package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.SheetVo
 * @Description :
 * @date :  2022.10.27 11:11
 */
@Data
public class SheetVo implements Serializable {
    private String sheetId;
    private String title;
}
