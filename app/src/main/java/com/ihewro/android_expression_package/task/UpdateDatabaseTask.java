package com.ihewro.android_expression_package.task;

import android.os.AsyncTask;

import com.blankj.ALog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.UpdateDatabaseListener;
import com.ihewro.android_expression_package.util.DateUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

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
    private int lastProgress;//上一个任务执行进度
    private int count  = 0;//需要扫描的总文件数目
    private int alCount = 0;//已经扫描的文件数目
    ExpressionFolder expressionFolder;
    public UpdateDatabaseTask(UpdateDatabaseListener listener) {
        this.listener = listener;
    }

    /**
     * 子线程，主要做计算等耗时任务
     * @param voids
     * @return
     */
    @Override
    protected Boolean doInBackground(Void... voids) {//子线程
        //扫描文件夹
        File appDir = new File(GlobalConfig.appDirPath);

        //首先获取该文件下的所有文件夹的所有文件数目，然后对每个文件夹和数据库的信息进行匹配处理
        // TODO:这个地方计算文件的数目大小好像有点问题啊
        final File dir[] = appDir.listFiles();
        for (int i = 0; i < dir.length; i++){
            if (dir[i].isDirectory()){
                ALog.d("count" + dir[i].list().length);
                count = count + dir[i].list().length;
            }
        }

        publishProgress(0);
        LitePal.deleteAll(ExpressionFolder.class);//删除表中所有的数据
        LitePal.deleteAll(Expression.class);//表情表有的外键丢失，上面那行代码是删除不掉的
        for (int i = 0;i < dir.length;i++){//app目录下面的每个目录分别进行扫描
            if (dir[i].isDirectory()){//表情包目录
                File[] files = dir[i].listFiles();//所有文件
                if (files.length > 0){//排除空文件夹
                    int currentFolderCount = 0;
                    expressionFolder = new ExpressionFolder(1,0,dir[i].getName(),null,null, DateUtil.getTimeStringByInt(dir[i].lastModified()),DateUtil.getTimeStringByInt(dir[i].lastModified()),new ArrayList<Expression>(),-1);
                    expressionFolder.save();

                    for (int j = 0; j<files.length;j++){
                        if (files[j].isFile() && files[j].getTotalSpace() > 0){//排除0B大小的文件
                            ALog.d("保存表情");
                            Expression expression = new Expression(1,files[j].getName() ,files[j].getAbsolutePath(),dir[i].getName(),expressionFolder);
                            expression.save();
                            currentFolderCount++;
                            alCount++;
                            publishProgress(alCount);//更新进度
                            expressionFolder.setCount(currentFolderCount);
                            expressionFolder.getExpressionList().add(expression);
                            expressionFolder.save();
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     * 更新进度条 | 主线程，doInBackground()中通过调用publishProgress，来切换到主线程执行这个函数，更新UI界面
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        if (count > 0){
            int progress = values[0];
            if (progress > lastProgress) {
                listener.onProgress(progress,count);
                lastProgress = progress;
            }
            listener.onProgress(lastProgress,count);
        }
    }

    /**
     * 任务刚启动 | 主线程
     */
    @Override
    protected void onPreExecute() {
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
