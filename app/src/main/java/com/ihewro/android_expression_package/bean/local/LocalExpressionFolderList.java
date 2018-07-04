package com.ihewro.android_expression_package.bean.local;

import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.ExpressionFolderList;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/04
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class LocalExpressionFolderList extends ExpressionFolderList {
    private List<LocalExpressionFolder> expressionFolderList;//表情包目录列表

    public List<LocalExpressionFolder> getExpressionFolderList() {
        return expressionFolderList;
    }

    public void setExpressionFolderList(List<LocalExpressionFolder> expressionFolderList) {
        this.expressionFolderList = expressionFolderList;
    }
}
