package com.ihewro.android_expression_package.task;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.util.FileUtil;

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
public class SaveFolderToLocalTask extends AsyncTask<List<Expression>, Integer, Boolean>{

    private MaterialDialog materialDialog;
    @SuppressLint("StaticFieldLeak")
    private Activity activity;
    private int count ;
    private String dirPath;
    private String dirName;
    public SaveFolderToLocalTask(Activity activity,int count,String dirName) {
        this.activity = activity;
        this.count = count;
        this.dirName = dirName;

        dirPath = GlobalConfig.appDirPath + dirName;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        materialDialog = new MaterialDialog.Builder(activity)
                .title("正在下载，请稍等")
                .content("陛下，耐心等下……")
                .progress(false, count, true)
                .build();

    }

    @SafeVarargs
    @Override
    protected final Boolean doInBackground(List<Expression>... lists) {
        publishProgress(-1);
        List<Expression> expressionList = lists[0];
        for (int i = 0;i<expressionList.size();i++){
            Expression expression = expressionList.get(i);
            final String targetPath = GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName();
            FileUtil.bytesSavedToFile(expression.getImage(),targetPath);
            publishProgress(i+1);
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        materialDialog.setTitle("操作成功");
        materialDialog.setContent("成功下载到路径" + dirPath);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values[0] == -1){
            materialDialog.show();
        }
        materialDialog.setProgress(values[0]);
        super.onProgressUpdate(values);
    }
}
