package com.ihewro.android_expression_package.bean.web;

import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.ExpressionFolderList;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class WebExpressionFolderList extends ExpressionFolderList {
    private List<WebExpressionFolder> webExpressionFolderList;//表情包目录列表

    public List<WebExpressionFolder> getWebExpressionFolderList() {
        return webExpressionFolderList;
    }

    public void setWebExpressionFolderList(List<WebExpressionFolder> webExpressionFolderList) {
        this.webExpressionFolderList = webExpressionFolderList;
    }
}
