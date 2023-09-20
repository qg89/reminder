package com.q.reminder.reminder.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author : Administrator
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.util.SearchFilesUtils
 * @Description :
 * @date :  2023.09.19 15:47
 */
public abstract class SearchFilesUtils {

    public static List<File> searchFiles(File folder, final String keyword) {
        List<File> result = new ArrayList<>();
        if (folder.isFile()) {
            result.add(folder);
        }
        File[] subFolders = folder.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()){
                    return true;
                }
                if (file.getName().toLowerCase().contains(keyword)) {
                    return true;
                }
                return false;
            }
        });
        if (subFolders != null) {
            for (File subFolder : subFolders) {
                if (subFolder.isFile()){
                    result.add(subFolder);
                }
                else {
                     result.addAll(searchFiles(subFolder, keyword));
                }
            }
        }
        return result;
    }
}
