package com.ihewro.android_expression_package.task;

import android.app.Activity;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.activity.ExpLocalFolderDetailActivity;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.TaskListener;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DeleteImageTask extends AsyncTask<Void,Void,Boolean>{
    private boolean isSingleDir;//是删除整个目录还是删除目录的一些图片
    private List<Expression> expressionList;
    private List<Expression> originExpList;
    private List<String> checkList;
    private String folderName;//文件夹名称
    private Activity activity;
    private MaterialDialog dialog = null;
    TaskListener listener;

    //给删除部分文件使用的构造器
    public DeleteImageTask(boolean isSingleDir, List<Expression> originExpList, List<String> checkList,String folderName,Activity activity, TaskListener listener) {
        this.isSingleDir = isSingleDir;
        this.originExpList = originExpList;
        this.checkList = checkList;
        this.folderName = folderName;
        this.listener = listener;
        this.activity = activity;

    }

    //删除整个目录的构造器
    public DeleteImageTask(boolean isSingleDir, String folderName,Activity activity, TaskListener listener) {
        this.isSingleDir = isSingleDir;
        this.folderName = folderName;
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        this.dialog = new MaterialDialog.Builder(activity)
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (isSingleDir){

            //删除文件夹
            FileUtil.delFolder(GlobalConfig.appDirPath + folderName);
            //删除数据库的内容
            LitePal.deleteAll(ExpressionFolder.class,"name = ?" ,folderName);
            LitePal.deleteAll(Expression.class,"folderName = ?", folderName);

        }else {//删除部分文件
            //组合中需要删除的文件list
            expressionList = new ArrayList<>();
            expressionList.clear();
            Collections.sort(checkList, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return Integer.parseInt(o2) - Integer.parseInt(o1);
                }
            });
            for (int i = 0; i < checkList.size(); i++) {
                expressionList.add(originExpList.get(Integer.parseInt(checkList.get(i))));
            }

            for (int i =0;i<expressionList.size();i++){
                FileUtil.deleteImageFromGallery(expressionList.get(i).getUrl());
                ALog.d("表情名称为" + expressionList.get(i).getName());
                LitePal.deleteAll(Expression.class,"name = ? and foldername = ?",expressionList.get(i).getName(),expressionList.get(i).getFolderName());
                //修改对应目录的数目
                List<ExpressionFolder> tempExpFolders = LitePal.where("name = ? and exist = ?",folderName, String.valueOf(1)).find(ExpressionFolder.class,true);

                if (tempExpFolders.get(0).getCount() == 1){//如果删除该表情，目录为空，直接把目录删掉
                    tempExpFolders.get(0).delete();
                }else {
                    tempExpFolders.get(0).setCount(tempExpFolders.get(0).getCount() - 1);
                    tempExpFolders.get(0).save();
                }
            }
        }
        UIUtil.autoBackUpWhenItIsNecessary();
        EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE));
        dialog.dismiss();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        dialog.dismiss();
        listener.onFinish(aBoolean);
    }
}
