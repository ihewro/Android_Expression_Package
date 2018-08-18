package com.ihewro.android_expression_package.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.SaveImageToGalleryListener;
import com.ihewro.android_expression_package.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/08/10
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class EditExpFolderNameTask extends AsyncTask<List<Expression>, Integer, Boolean> {

    private MaterialDialog materialDialog;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private int count ;
    private String originDirName;
    private String targetDirName;
    private SaveImageToGalleryListener listener;
    public EditExpFolderNameTask(Activity activity, int count,String originDirName,String targetDirName,SaveImageToGalleryListener listener) {
        this.activity = activity;
        this.count = count;
        this.originDirName = originDirName;
        this.targetDirName = targetDirName;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        materialDialog = new MaterialDialog.Builder(activity)
                .title("正在修改表情包名称，请稍等")
                .content("陛下，耐心等下……")
                .progress(false, count, true)
                .build();
    }

    @Override
    protected Boolean doInBackground(List<Expression>... lists) {
        List<Expression> expressionList = lists[0];
        //从数据库查找该表情包
        ExpressionFolder expressionFolder = LitePal.where("name = ?",originDirName).find(ExpressionFolder.class).get(0);
        expressionFolder.setName(targetDirName);
        expressionFolder.save();

        for (int i = 0; i < expressionList.size();i++){
            Expression expression = expressionList.get(i);
            expression.setFolderName(targetDirName);
            expression.setExpressionFolder(expressionFolder);
            expression.save();
            publishProgress(i+1);
        }
        expressionFolder.setExpressionList(expressionList);
        expressionFolder.save();
        return true;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values[0] == -1){
            materialDialog.show();
        }else {
            materialDialog.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        materialDialog.setTitle("修改名称成功");
        listener.onFinish(aBoolean);
        UIUtil.autoBackUpWhenItIsNecessary();
        EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE));
    }
}
