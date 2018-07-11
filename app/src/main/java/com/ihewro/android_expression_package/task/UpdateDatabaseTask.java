package com.ihewro.android_expression_package.task;

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
    private Activity activity;
    private int lastProgress;//上一个任务执行进度
    private int count  = 0;//需要扫描的总文件数目
    private int alCount = 0;//已经扫描的文件数目
    ExpressionFolder expressionFolder;
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
        List<String> existFolderIdList = new ArrayList<>();
        for (int i = 0;i < dir.length;i++){//app目录下面的每个目录分别进行扫描
            if (dir[i].isDirectory() && !Objects.equals(dir[i].getName(), "database")){//表情包目录//排除掉存放数据库备份的目录
                File[] files = dir[i].listFiles();//目录下的所有文件
                if (files.length > 0){//排除空文件夹
                    int currentFolderCount = 0;
                    Boolean isExistFolder = false;
                    List<ExpressionFolder> tempExpressionFolderList = LitePal.where("name = ? and exist = ?",dir[i].getName(), String.valueOf(1)).find(ExpressionFolder.class,true);
                    if (tempExpressionFolderList.size() > 0){//首先查询数据库中有没有这个目录
                        ALog.d("当前目录存在");
                        expressionFolder = tempExpressionFolderList.get(0);
                        existFolderIdList.add(String.valueOf(expressionFolder.getId()));//把数据库中真实存在的目录id存进去
                        isExistFolder = true;
                    }else {
                        ALog.d("当前目录不存在");
                        expressionFolder = new ExpressionFolder(1,0,dir[i].getName(),null,null, DateUtil.getTimeStringByInt(dir[i].lastModified()),DateUtil.getTimeStringByInt(dir[i].lastModified()),new ArrayList<Expression>(),-1);
                        expressionFolder.save();
                        existFolderIdList.add(String.valueOf(expressionFolder.getId()));//把数据库中真实存在的目录id存进去
                    }


                    if (expressionFolder.isSaved()){
                        ALog.d("该对象已经是持续化了");
                    }else {
                        ALog.d("出了点问题");
                    }

                    List<String> existExpIdList = new ArrayList<>();
                    for (int j = 0; j<files.length;j++){
                        if (files[j].isFile() && files[j].getTotalSpace() > 0){//排除0B大小的文件
                            ALog.d("保存表情");
                            //目录在数据库中存在的前提下检查图片描述是否存在，不存在的话才调用接口
                            boolean isExistExp = false;
                            if (isExistFolder){
                                List<Expression> tempExpList = LitePal.where("name = ? and foldername = ?",files[j].getName(),dir[i].getName()).find(Expression.class,true);
                                if (tempExpList.size() > 0){//表情库里有这个表情信息
                                    Expression expression = tempExpList.get(0);
                                    isExistExp = true;
                                    expression.setExpressionFolder(expressionFolder);
                                    MyDataBase.saveExpImage(expression,false);
                                    existExpIdList.add(String.valueOf(expression.getId()));
                                    //判断是否有描述，没有的话需要获取描述
                                    if (expression.getDesStatus() == 0){
                                        new GetExpDesTask(activity,true).execute(expression);
                                    }

                                }
                            }
                            if (!isExistExp){//表情中中没这个表情信息
                                ALog.d("表情库中没这个表情信息" + files[j].getName());
                                InputStream is = null;
                                byte[] bytes = null;
                                try {
                                    is = new FileInputStream(files[j]);
                                    bytes = UIUtil.InputStreamTOByte(is);
                                    is.close();
                                } catch (java.io.IOException e) {
                                    e.printStackTrace();
                                }
                                Expression expression = new Expression(1,files[j].getName() ,files[j].getAbsolutePath(),dir[i].getName(),expressionFolder,bytes);
                                expression.save();
                                existExpIdList.add(String.valueOf(expression.getId()));

                                new GetExpDesTask(activity,true).execute(expression);
                                currentFolderCount++;
                                expressionFolder.setCount(currentFolderCount);
                                expressionFolder.getExpressionList().add(expression);
                                expressionFolder.save();
                            }
                            alCount++;
                            publishProgress(alCount);//更新进度
                        }
                    }
                    //在表情的表中删除目录中不存在的图片信息
                    if (isExistFolder){
                        String condition = "";
                        for (int k = 0;k<existExpIdList.size();k++){
                            if (k != existExpIdList.size()-1){
                                condition += "?,";
                            }else {
                                condition += "?";
                            }
                        }
                        existExpIdList.add(0,"foldername = ? and id not IN (" + condition + ")");
                        existExpIdList.add(1,dir[i].getName());
                        LitePal.deleteAll(Expression.class,existExpIdList.toArray(new String[existExpIdList.size()]));
                    }
                }
            }
        }


        //在表情包的表中把实际不存在的目录从表中删除
        String condition = "";
        for (int k = 0;k<existFolderIdList.size();k++){
            if (k != existFolderIdList.size()-1){
                condition += "?,";
            }else {
                condition += "?";
            }
        }
        existFolderIdList.add(0,"id not IN ("+condition+")");
        LitePal.deleteAll(ExpressionFolder.class,existFolderIdList.toArray(new String[existFolderIdList.size()]));
        EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE));
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
            listener.onProgress(lastProgress,count);
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
