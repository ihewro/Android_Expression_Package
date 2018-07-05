package com.ihewro.android_expression_package;

import android.widget.Toast;

import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.UpdateDatabaseListener;
import com.ihewro.android_expression_package.util.UIUtil;

import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/04
 *     desc   : 使用sqlite数据库去管理本地的表情包信息，这样速度更快，操作更简单。
 *     version: 1.0
 * </pre>
 */
public class MyDataBase {

    /**
     * 增加一个表情包的记录
     */
    public static void addStorageExpFolderName(ExpressionFolder expressionFolder, List<Expression> expressionList){
        ExpressionFolder folder = new ExpressionFolder(1,expressionFolder.getCount(),expressionFolder.getName(),expressionFolder.getOwner(),expressionFolder.getOwnerAvatar(),expressionFolder.getCreateTime(),expressionFolder.getUpdateTime(), expressionList,expressionFolder.getDir());
        if (folder.save()){
            Toasty.success(UIUtil.getContext(),"表情包合集保存本地成功",Toast.LENGTH_SHORT).show();

        }else {
            Toasty.error(UIUtil.getContext(),"存储数据失败，这种异常极少方式，该错误将导致app表情包数据显示错误，请手动同步以便获取最新数据。",Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 重新更新数据的信息，一般在错误发生或调用该函数
     */
    public static void updateDatabaseByRefresh(UpdateDatabaseListener callback){


        callback.onFinished();
    }
}
