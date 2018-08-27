package com.ihewro.android_expression_package.task;

import android.app.Activity;
import android.os.AsyncTask;

import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.MyDataBase;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.TaskListener;
import com.ihewro.android_expression_package.util.UIUtil;
import com.zhihu.matisse.Matisse;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/08/27
 *     desc   : 把本地的一组图片加入到指定的表情包目录中
 *     version: 1.0
 * </pre>
 */
public class AddExpListToExpFolderTask extends AsyncTask<Void,String,Boolean> {

    private Activity activity;

    private List<String> addExpList;
    private List<Expression> expressionList;
    private TaskListener listener;
    private String folderName;


    public AddExpListToExpFolderTask(Activity activity, List<String> addExpList, String folderName, TaskListener listener) {
        this.activity = activity;
        this.addExpList = addExpList;
        this.listener = listener;
        this.folderName = folderName;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        //把图片加入到图库中
        for (int i = 0; i < addExpList.size(); i++) {
            File tempFile = new File(addExpList.get(i));
            String fileName = tempFile.getName();
            final Expression expression = new Expression(1, fileName, "", folderName);
            if (!MyDataBase.addExpressionRecord(expression, tempFile)) {
                publishProgress(GlobalConfig.ERROR_FILE_LIMIT);
            }
        }
        UIUtil.autoBackUpWhenItIsNecessary();
        EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE));

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

        if (values[0].equals(GlobalConfig.ERROR_FILE_LIMIT)){
            Toasty.info(UIUtil.getContext(), values[1] + "文件大小太大，将不会存储").show();
        }
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        listener.onFinish(aBoolean);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
}
