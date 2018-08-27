package com.ihewro.android_expression_package.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.TaskListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/08/23
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ShowAllExpFolderTask extends AsyncTask<Void, Integer, Boolean> {

    private TaskListener listener;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private MaterialDialog dialog;
    private String folderName;
    private List<String> folderNameList = new ArrayList<>();//表情包名称列表
    private Boolean isReady = false;//是否表情包目录已经加载完毕
    private Boolean isShowDefault = true;


    public ShowAllExpFolderTask(TaskListener listener,Activity activity) {
        this.listener = listener;
        this.activity = activity;
    }

    public ShowAllExpFolderTask(TaskListener listener, Activity activity, String folderName, Boolean isShowDefault) {
        this.listener = listener;
        this.activity = activity;
        this.folderName = folderName;
        this.isShowDefault = isShowDefault;
    }

    @Override
    protected void onPreExecute() {
        //显示选择表情包列表的对话框
        dialog = new MaterialDialog.Builder(activity)
                .title("添加到表情包")
                .content("加载表情目录中稍等")
                .items()
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (isReady){
                            if (which != 0 ||!isShowDefault){//如果显示默认名称的话，在第0项不需要赋值
                                folderName = (String) dialog.getItems().get(which);
                            }
                            ALog.d(folderName);
                            listener.onFinish(folderName);
                        }

                    }
                })
                .show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        folderNameList.clear();
        if (isShowDefault){
            folderNameList.add(folderName + "(默认)");
        }
        List<ExpressionFolder> expressionFolderList = LitePal.select("name").order("ordervalue").find(ExpressionFolder.class);
        for (ExpressionFolder expFolder:
                expressionFolderList) {
            folderNameList.add(expFolder.getName());
        }

        return null;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        ALog.d("主线程");
        int size = folderNameList.size();
        dialog.setContent("单击添加到指定的表情包下");
        dialog.setItems(folderNameList.toArray(new String[size]));
        dialog.notifyItemsChanged();
        isReady = true;
    }
}
