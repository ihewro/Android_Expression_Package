package com.ihewro.android_expression_package.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import com.blankj.ALog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.MyDataBase;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.UpdateDatabaseListener;
import com.ihewro.android_expression_package.util.DateUtil;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/05
 *     desc   : 更新数据库
 *     version: 1.0
 * </pre>
 */
public class UpdateDatabaseTask  extends AsyncTask<Void, Integer, Boolean> {

    private UpdateDatabaseListener listener;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private int lastProgress;//上一个任务执行进度
    private int count  = 0;//需要扫描的总文件数目
    private int alCount = 0;//已经扫描的文件数目
    public UpdateDatabaseTask(Activity activity, UpdateDatabaseListener listener) {
        this.listener = listener;
        this.activity = activity;
    }

    /**
     * 子线程，主要做计算等耗时任务
     * @param voids
     * @return
     */
    @Override
    protected Boolean doInBackground(Void... voids) {//子线程
        //扫描数据库
        count = LitePal.count(Expression.class);//计算总的数目
        alCount = 0;
        publishProgress(alCount);

        //循环遍历每个目录，修正目录的表情包数目
        List<ExpressionFolder> expressionFolderList = LitePal.findAll(ExpressionFolder.class);
        for (ExpressionFolder expressionFolder:
             expressionFolderList) {
            List<Expression> expressions = LitePal.select("id","name","foldername","status","url","desstatus","description").where("foldername = ?",expressionFolder.getName()).find(Expression.class,true);


            //修正表情包的数目
            expressionFolder.setCount(expressions.size());
            expressionFolder.save();
        }

        //循环遍历每个表情，修正描述内容，和删除无用的表情项
        List<Expression> expressionList = LitePal.select("id","name","foldername","status","url","desstatus","description").find(Expression.class,true);
        for (Expression expression: expressionList){
            List<ExpressionFolder> expressionFolders = LitePal.where("name = ?",expression.getFolderName()).find(ExpressionFolder.class);
            if (expressionFolders.size() <=0){
                //1. 删除表情表中游离的表情，即表情目录名称在表情目录表中不存在
                expression.delete();
            }else {
                //2. 没有表情描述，自动识别文字
                if (expression.getDesStatus() == 0){
                    new GetExpDesTask(activity,true).execute(expression);
                }

                //3. 地址修正，如果本地文件不存在，则url置为空，否则才置为本地路径
                File local = new File(GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName());
                if (!local.exists()){
                    expression.setUrl("");
                }else {
                    expression.setUrl(GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName());
                }
                expression.save();
            }

            alCount++;
            publishProgress(alCount);
        }



        //5. 删除temp文件夹
        FileUtil.delFolder(GlobalConfig.appTempDirPath);


        return true;
    }


    /**
     * 更新进度条 | 主线程，doInBackground()中通过调用publishProgress，来切换到主线程执行这个函数，更新UI界面
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        ALog.d("更新进度");
        if (count > 0){
            int progress = values[0];
            if (progress > lastProgress) {
                listener.onProgress(progress,count);
                lastProgress = progress;
            }
            ALog.d("总数目" + count);
            //listener.onProgress(lastProgress,count);
        }
    }

    /**
     * 任务刚启动 | 主线程
     */
    @Override
    protected void onPreExecute() {
        ALog.d("开始任务");
        listener.onStart();
    }

    /**
     * 任务结束 | 主线程
     * @param aBoolean
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {//主线程
        listener.onFinished();
    }
}
