package com.ihewro.android_expression_package.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.blankj.ALog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.MyDataBase;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.SaveImageToGalleryListener;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/06
 *     desc   : 1.下载图片到本地 2. 数据库信息更新
 *     version: 1.0
 * </pre>
 */
public class SaveImageToGalleryTask extends AsyncTask<Expression, Integer, Boolean>{

    private SaveImageToGalleryListener listener;
    private Activity activity;

    public SaveImageToGalleryTask(SaveImageToGalleryListener listener, Activity activity) {
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(Expression... expressions) {
        Expression expression = expressions[0];
        final String targetPath = GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName();
        boolean result;
        if (expression.getStatus() == 1){//sd卡图片
            if (expression.getImage() == null || expression.getImage().length == 0){
                expression = LitePal.find(Expression.class,expression.getId());//查询出二进制图片
            }
            ALog.d("id = " + expression.getId() + "大小" + expression.getImage().length);
            result =  FileUtil.bytesSavedToFile(expression.getImage(),targetPath);
            return result;
        }else if (expression.getStatus() == 2){//网络来源的图片
            try {
                File imageFile = Glide.with(activity).asFile()
                        .apply(RequestOptions.priorityOf(Priority.HIGH).onlyRetrieveFromCache(true))
                        .load(expression.getUrl())
                        .submit().get();
                if(imageFile != null && imageFile.exists()){
                    MyDataBase.addExpressionRecord(expression,imageFile);
                    UIUtil.autoBackUpWhenItIsNecessary();
                    EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE));
                    //保存单张图片的时候不仅保存到数据库里也保存到本地文件夹
                    File targetFile = new File(targetPath);
                    result = FileUtil.copyFileToTarget(imageFile,targetFile);
                }else {
                    result = false;
                }
            }catch (Exception e){
                result = false;
                e.printStackTrace();
            }

            return result;
        }else if (expression.getStatus() == 3){//本机图片
            return true;
        }else {//未知来源图片
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onFinish(aBoolean);
    }
}
