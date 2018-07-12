package com.ihewro.android_expression_package.task;

import android.os.AsyncTask;

import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.ShowExpListListener;

import org.litepal.LitePal;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ShowExpListTask extends AsyncTask<String,Void,List<Expression>>{

    ShowExpListListener listener;

    public ShowExpListTask(ShowExpListListener listener) {
        this.listener = listener;
    }

    @Override
    protected List<Expression> doInBackground(String... strings) {
        String name = strings[0];
        return  LitePal.select("id","name","foldername","status","url","expressionfolder_id","desstatus","description").where("foldername = ?",name).find(Expression.class);
    }

    @Override
    protected void onPostExecute(List<Expression> expressions) {
        listener.onFinish(expressions);
    }
}
