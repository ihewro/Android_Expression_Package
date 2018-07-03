package com.ihewro.android_expression_package.bean;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/02
 *     desc   : 总的表情包信息管理类
 *     version: 1.0
 * </pre>
 */
public class ExpressionFolderList {
    private int count;//表情包目录数目
    private List<ExpressionFolder> expressionFolderList;//表情包目录列表

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ExpressionFolder> getExpressionFolderList() {
        return expressionFolderList;
    }

    public void setExpressionFolderList(List<ExpressionFolder> expressionFolderList) {
        this.expressionFolderList = expressionFolderList;
    }
}
