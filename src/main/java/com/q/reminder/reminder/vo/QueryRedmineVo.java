package com.q.reminder.reminder.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.vo.QueryRedmineVo
 * @Description :
 * @date :  2022.10.19 09:49
 */
@Data
public class QueryRedmineVo implements Serializable {

    private static final long serialVersionUID = -2299536628917442487L;
    private Set<String> projects;
    private List<String> noneStatusList;
    private String apiAccessKey;
    private String redmineUrl;
    private Integer expiredDay;
    private Boolean containsStatus;
}
