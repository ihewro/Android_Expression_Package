package com.ihewro.android_expression_package;

import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.OneDetailList;
import com.ihewro.android_expression_package.callback.UpdateDatabaseListener;
import com.ihewro.android_expression_package.util.DateUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
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
     * 把一个表情信息加入到数据库
     * @param expression
     * @return
     */
    public static boolean addExpressionRecord(Expression expression){
        //1. 检查有没有表情对应的目录
        List<ExpressionFolder> expressionFolderList = LitePal.where("name = ? and exist = ?",expression.getFolderName(), String.valueOf(1)).find(ExpressionFolder.class,true);
        ExpressionFolder expressionFolder = null;//当前表情的目录的持久化对象

        Expression currentExpression;//当前表情的持久化对象
        //2. 检查该目录中有没有该表情名称
        if (expressionFolderList.size () == 1){
            expressionFolder = expressionFolderList.get(0);
            List<Expression> expressionList = LitePal.where("name = ?",expression.getName()).find(Expression.class);
            if (expressionList.size() >0){//有该表情的信息就不用管了
                return true;
            }
            ALog.d("目录存在，但是表情不存在");
        }else if (expressionFolderList.size() <=0){//没有该目录信息
            expressionFolder = new ExpressionFolder(1,0,expression.getFolderName(),null,null, DateUtil.getNowDateStr(),null,new ArrayList<Expression>(),-1);
            expressionFolder.save();
            ALog.d("目录和表情都没有的");
        } else {
            return false;//这种错误几乎不会发生，除非数据库的错误严重错乱
        }
        //3. 把表情的信息存储进去,执行这里的时候有两种情况，一种是目录和表情都没有的。一种目录存在，但是表情不存在。
        currentExpression = new Expression(1,expression.getName(),GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName(),expression.getFolderName(),expressionFolder);
        currentExpression.save();

        expressionFolder.setCount(expressionFolder.getCount() + 1);

        if (expressionFolder.getExpressionList() == null || expressionFolder.getExpressionList().size() == 0){
            List<Expression> tempExpressionList = new ArrayList<>();
            expressionFolder.setExpressionList(tempExpressionList);
            expressionFolder.getExpressionList().add(currentExpression);
            ALog.d("表情数目为0");
        }else {
            ALog.d("表情数目不为0");
            expressionFolder.getExpressionList().add(currentExpression);
        }
        expressionFolder.save();



        return true;
    }


    public static boolean isNeedGetOnes(){
        //获取one数据库信息
        List<OneDetailList> oneDetailListList = LitePal.findAll(OneDetailList.class);
        boolean flag = false;
        if (oneDetailListList.size()>0){
            if (DateUtil.isTimeout(DateUtil.getNowDateStr(),oneDetailListList.get(0).getDate())){//超时了，需要更新数据库信息
                flag = true;
            }
        }else {//数据库中没有内容获取新的请求，更新数据库信息
            flag = true;
        }

        if (flag){
            ALog.d("需要重新请求ones数据");
        }
        return flag;
    }
}
