package com.ihewro.android_expression_package;

import android.widget.Toast;

import com.blankj.ALog;
import com.ihewro.android_expression_package.activity.ExpLocalFolderDetailActivity;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.OneDetailList;
import com.ihewro.android_expression_package.callback.UpdateDatabaseListener;
import com.ihewro.android_expression_package.util.DateUtil;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

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
    public static boolean addExpressionRecord(Expression expression,File source){

        byte[] bytes = fileToCompressedBytes(source);
        if (bytes == null){
            return false;
        }else {
            return addExpressionRecord(expression,bytes);
        }
    }

    private static byte[] fileToCompressedBytes(File source){
        //先进行图片压缩，避免数据太大，导致读取问题
        File compressToFile;
        compressToFile = FileUtil.returnCompressExp(source);
        ALog.d("压缩后的路径" + compressToFile.getAbsolutePath() + "大小" + compressToFile.length());
        byte[] bytes = FileUtil.fileToBytes(compressToFile);
        ALog.d("文件大小为" + bytes.length);
        if (bytes.length>2060826){
            return null;
        }else {
            //因为compressToFile 和 source是同一个文件，所以先判断下，再决定是否删除
            if (compressToFile.exists() && !Objects.equals(compressToFile.getAbsolutePath(), source.getAbsolutePath())){
                compressToFile.delete();
            }
            return bytes;
        }
    }



    public static boolean addExpressionRecord(Expression expression,byte[] source) {
        //1. 检查有没有表情对应的目录
        List<ExpressionFolder> expressionFolderList = LitePal.where("name = ? and exist = ?",expression.getFolderName(), String.valueOf(1)).find(ExpressionFolder.class);
        ExpressionFolder expressionFolder;//当前表情的目录的持久化对象

        Expression currentExpression;//当前表情的持久化对象

        //2. 检查该目录中有没有该表情名称
        if (expressionFolderList.size () == 1){
            expressionFolder = expressionFolderList.get(0);
            List<Expression> expressionList = queryExpListByNameAndFolderName(false,expression.getName(),expression.getFolderName());
            if (expressionList.size() >0){//有该表情的信息就不用管了
                currentExpression = expressionList.get(0);
                saveExpImage(currentExpression,source,false);
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
        currentExpression = new Expression(1,expression.getName(),GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName(),expression.getFolderName());
        currentExpression.save();
        saveExpImage(currentExpression,source,false);



        expressionFolder.setCount(expressionFolder.getCount() + 1);

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

    public static void saveExpImage(Expression expression,File file, boolean isForce) {
        byte[] bytes = FileUtil.fileToBytes(file);
        saveExpImage(expression,bytes,isForce);
    }

    /**
     * 把图片存储到数据库的二进制中
     * @param expression
     * @param bytes
     * @param isForce
     */
    public static void saveExpImage(Expression expression,byte[] bytes, boolean isForce){
        if (expression.isSaved()){
            if (isForce || (expression.getImage() == null || expression.getImage().length == 0)){
                expression.setImage(bytes);
                ALog.d(bytes);
                expression.save();
            }
        }else {
            ALog.d("expression 不是持久化对象");
        }
    }

    public static List<Expression> queryExpListByNameAndFolderName(boolean isImage,String name,String folderName){
        if (isImage){
            return LitePal.where("name = ? and foldername = ?",name,folderName).find(Expression.class);
        }else {
            return LitePal.select("id","name","foldername","status","url","desstatus","description").where("name = ? and foldername = ?",name,folderName).find(Expression.class);
        }
    }

}
