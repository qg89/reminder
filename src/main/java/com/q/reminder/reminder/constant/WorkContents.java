package com.q.reminder.reminder.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.constant.WorkContents
 * @Description :
 * @date :  2023/8/10 09:56
 */
public interface WorkContents {

   static Map<String, Integer> work() {
        Map<String, Integer> map = new HashMap<>();
        map.put("202301", 120);
        map.put("202302", 160);
        map.put("202303", 184);
        map.put("202304", 160);
        map.put("202305", 168);
        map.put("202306", 168);
        map.put("202307", 168);
        map.put("202308", 184);
        map.put("202309", 160);
        map.put("202310", 152);
        map.put("202311", 176);
        map.put("202312", 168);
        return map;
    }
}
