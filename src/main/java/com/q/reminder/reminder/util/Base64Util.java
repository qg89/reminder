package com.q.reminder.reminder.util;

import cn.hutool.core.date.DateUtil;
import lombok.extern.log4j.Log4j2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Base64;
import java.util.UUID;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.Base64Util
 * @Description :
 * @date :  2022.11.09 10:30
 */
@Log4j2
public class Base64Util {
    /**
     * @Description 将Base64字符串转为文件对象
     * @Param [base64]
     * @Return java.io.File
     * @Author Louis
     * @Date 2022/07/10 17:25
     */
    public static File base64ToFile(String base64) {
        try {
            // Base64解码
            byte[] b = Base64.getDecoder().decode(base64);
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    //调整异常数据
                    b[i] += 256;
                }
            }
            // 对文件重命名，设定为当前系统时间的毫秒数加UUID
            String newFileName = System.currentTimeMillis() + "-" + UUID.randomUUID() + ".jpeg";
            // 放在本地临时文件目录
            String localFilePath = String.format("%stemp%s%s", File.separator, File.separator, DateUtil.today());
            File filePath = new File(localFilePath);
            if (!filePath.exists()) {
                //　mkdirs(): 创建多层目录
                filePath.mkdirs();
            }
            // 文件全限定名
            String path = localFilePath + File.separator + newFileName;
            // 将数据通过流写入文件
            OutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return new File(path);
        } catch (Exception e) {
            log.error(e);
        }
        return null;
    }
}
