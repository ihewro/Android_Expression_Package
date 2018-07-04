package com.ihewro.android_expression_package.bean.local;

import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;

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
public class LocalExpressionFolder extends ExpressionFolder {


    public static final int EXP_FOLDER_INVALID = -1;
    public static final int EXP_FOLDER_NORMAL = 1;
    public static final int EXP_FOLDER_DELETED = -2;


    /**
     * 1 表示表情包正常存在
     * -1 表示表情包无效，不是在app中删除的，会在我的表情包显示该项，但是不会显示具体的细节
     * -2 表情表情包已经删除，不会在[我的表情包]中显示该项
     */
    private int exist;


    public LocalExpressionFolder(){

    }

    public LocalExpressionFolder(int count, String name, String owner, String ownerAvatar, String createTime, String updateTime, List<Expression> expressionList, int exist) {
        super(count, name, owner, ownerAvatar, createTime, updateTime, expressionList);
        this.exist = exist;
    }

    public LocalExpressionFolder(String owner, String ownerAvatar, int exist) {
        super(owner, ownerAvatar);
        this.exist = exist;
    }


    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }
}
