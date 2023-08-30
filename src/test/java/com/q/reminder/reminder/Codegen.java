package com.q.reminder.reminder;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.mybatisflex.codegen.config.TableDefConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.Codegen
 * @Description :
 * @date :  2023/7/27 15:36
 */
public class Codegen {
    static String packageName = "com.q.reminder.reminder";
    
    
    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("mpw:YAU7VUQTQlBTP/D7DYdMI5A5u1zqyh85JI/hVpPjrLzCpZnihi+gruSUQTzq8hl9p8mY2czOCrvHMrNNn5hE2869zSbitJStXAKwXQKs6yzl2JhuHSDinw5IyD2v7td4WFtaEQNW8VYwfi1CT17S4/Zf44Yht4Y0Pm/gaRPgSokkAa2s5qe1tu3m1oF4nCFiWsPOwe2hWdR6b6hI5L1Dca0CZCVTVW4dBM+ceuRmoEHR68ILnwSrc+jk7STgvjU+aQ3O/dFxmN7D2qLattxa5wEJjr9Xg5hxczIrC75/SBfst0IOmzqpQdgOyplnL6VGc7KZmcqxKunLwVhdNZqpCQ==");
        dataSource.setUsername("mpw:Jrfx9m/vVYtI1PLKUIeYlA==");
        dataSource.setPassword("mpw:tZLGHiKWlhtIyjhAlDJgQw==");

        //创建配置内容，两种风格都可以。
        GlobalConfig globalConfig = createGlobalConfigUseStyle();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();

        //设置根包
        globalConfig.getPackageConfig()
                .setBasePackage(packageName);

        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
//        globalConfig.getStrategyConfig()
//                .setGenerateSchema("reminder")
////                .setTablePrefix("tb_")
//                .setGenerateTable("ttt_test");

        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true);

        globalConfig.enableTableDef();
        globalConfig.setTableDefPropertiesNameStyle(TableDefConfig.NameStyle.LOWER_CAMEL_CASE);

        //设置生成 mapper
        globalConfig.enableMapper();

        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("id");
        columnConfig.setLarge(true);
        columnConfig.setVersion(true);
        globalConfig.getStrategyConfig()
                .setColumnConfig("ttt_test", columnConfig);

        return globalConfig;
    }
}
