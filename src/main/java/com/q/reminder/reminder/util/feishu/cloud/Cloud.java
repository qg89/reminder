package com.q.reminder.reminder.util.feishu.cloud;

import com.q.reminder.reminder.util.feishu.BaseFeishu;
import com.q.reminder.reminder.util.feishu.cloud.documents.Documents;
import com.q.reminder.reminder.util.feishu.cloud.documents.ExportFile;
import com.q.reminder.reminder.util.feishu.cloud.documents.Upload;
import com.q.reminder.reminder.util.feishu.cloud.space.Space;
import com.q.reminder.reminder.util.feishu.cloud.table.Table;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.feishu.cloud.Cloud
 * @Description :
 * @date :  2023.03.27 17:13
 */
public class Cloud extends BaseFeishu {

    private  static Cloud instance;
    private Cloud(){
        super();
    }
    public static synchronized Cloud getInstance(){
        if (instance == null) {
            instance = new Cloud();
        }
        return instance;
    }

    public Documents documents() {
        return Documents.getInstance();
    }
    public Space space() {
        return Space.getInstance();
    }

    public Table table() {
        return Table.getInstance();
    }

    public Upload upload() {
        return Upload.getInstance();
    }
    public ExportFile export() {
        return ExportFile.getInstance();
    }
}
