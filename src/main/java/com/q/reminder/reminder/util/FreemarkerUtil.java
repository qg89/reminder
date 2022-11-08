package com.q.reminder.reminder.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.log4j.Log4j2;

import java.io.StringWriter;
import java.util.Map;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.FreemarkerUtil
 * @Description :
 * @date :  2022.11.03 10:50
 */
@Log4j2
public abstract class FreemarkerUtil {

    // 类加载器，用于获取项目目录
    private static final ClassLoader CLASS_LOADER = FreemarkerUtil.class.getClassLoader();
    // 模板存放的目录
    private static final String BASE_PATH = "templates/echarts";

    /**
     * @Description 加载模板并生成ECharts的option数据字符串
     * @Param [templateFileName, data]
     * @Return java.lang.String
     * @Author Louis
     * @Date 2022/07/10 17:16
     */
    public static String generate(String templateFileName, Map<String, Object> data) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        // 设置默认编码
        configuration.setDefaultEncoding("UTF-8");
        // 将 data 写入模板并返回
        try {
            StringWriter writer = new StringWriter();
            // 设置模板所在目录，设置目录打成jar包后无法读取，所以使用类加载器
            // configuration.setDirectoryForTemplateLoading(new File(BASE_PATH));
            configuration.setClassLoaderForTemplateLoading(CLASS_LOADER, BASE_PATH);
            // 生成模板对象
            Template template = configuration.getTemplate(templateFileName);
            template.process(data, writer);
            writer.flush();
            return writer.getBuffer().toString();
        } catch (Exception e) {
            log.error("解析模板异常：{}", e);
        }
        return null;
    }
}
