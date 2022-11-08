package com.q.reminder.reminder.util;

import cn.hutool.core.codec.Base64Decoder;
import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.test.EchartsUtil
 * @Description :
 * @date :  2022.11.03 10:58
 */
@Log4j2
public abstract class EchartsUtil {

    private static final String URL = "http://localhost:6666";
    private static final String SUCCESS_CODE = "1";

    private static String generateEchartsBase64(String option) throws IOException {
        String base64 = "";
        if (option == null) {
            return base64;
        }
        option = option.replaceAll("\\s+", "").replaceAll("\"", "'");

        // 将option字符串作为参数发送给echartsConvert服务器
        Map<String, Object> params = new HashMap<>();
        params.put("opt", option);
        String response = HttpUtil.post(URL, params);

        // 解析echartsConvert响应
        JSONObject responseJson = JSON.parseObject(response);
        String code = responseJson.getString("code");

        // 如果echartsConvert正常返回
        if (SUCCESS_CODE.equals(code)) {
            base64 = responseJson.getString("data");
        }
        // 未正常返回
        else {
            String string = responseJson.getString("msg");
            throw new RuntimeException(string);
        }

        return base64;
    }

    private static File generateImage(String base64, String path) throws IOException {
        BufferedOutputStream bos = null;
        java.io.FileOutputStream fos = null;
        File file = new File(path);
        try {
            byte[] b = Base64Decoder.decode(base64);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(b);
        } finally {
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
        return file;
    }

    /**
     * 获取文件
     * @param datas
     * @param ftl
     * @return
     */
    @Nullable
    public static File getFile(HashMap<String, Object> datas, String ftl) {
        // 生成option字符串
        String option = FreemarkerUtil.generate(ftl, datas);
        // 根据option参数
        String base64 = null;
        try {
            base64 = generateEchartsBase64(option);
            java.net.URL url = WeeklyProjectUtils.class.getClassLoader().getResource("templates/file");
            String fileName = url.getPath() + "/" + UUID.fastUUID() + ".png";
            return generateImage(base64, fileName);
        } catch (IOException e) {
            log.error(e);
        }
        return null;
    }
}
