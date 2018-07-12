package com.ihewro.android_expression_package.task;

import android.os.AsyncTask;

import com.blankj.ALog;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.GetExpImageListener;

import org.litepal.LitePal;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GetExpImageTask extends AsyncTask<Integer,Void,Expression> {

    private GetExpImageListener listener;
    private boolean isQueryFolder;

    public GetExpImageTask(GetExpImageListener listener) {
        this.listener = listener;
    }

    public GetExpImageTask(GetExpImageListener listener, boolean isQueryFolder) {
        this.listener = listener;
        this.isQueryFolder = isQueryFolder;
    }

    @Override
    protected Expression doInBackground(Integer... integers) {
        if (isQueryFolder){
            return LitePal.find(Expression.class,integers[0],true);
        }else {
            ALog.d("id = " + integers[0]);
            return LitePal.find(Expression.class,integers[0],false);
        }
    }

    @Override
    protected void onPostExecute(Expression expression) {
        listener.onFinish(expression);
    }
}
