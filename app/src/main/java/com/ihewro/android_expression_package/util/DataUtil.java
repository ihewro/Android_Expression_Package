package com.ihewro.android_expression_package.util;

import com.ihewro.android_expression_package.bean.local.LocalExpressionFolder;
import com.ihewro.android_expression_package.bean.local.database.DatabaseExpFolder;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/05
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DataUtil {

    public static List<LocalExpressionFolder> databaseExpListToLocal(List<DatabaseExpFolder> databaseExpFolderList){
        List<LocalExpressionFolder> localExpressionFolderList = new ArrayList<>();
        for (DatabaseExpFolder databaseExpFolder: databaseExpFolderList ) {
            //localExpressionFolderList.add(new LocalExpressionFolder(databaseExpFolder.getCount()));
        }

        return localExpressionFolderList;
    }
}
