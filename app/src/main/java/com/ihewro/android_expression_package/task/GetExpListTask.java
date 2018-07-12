package com.ihewro.android_expression_package.task;

import android.os.AsyncTask;

import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.GetExpListListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GetExpListTask extends AsyncTask<String,Void,List<Expression>>{

    GetExpListListener listener;
    private boolean isImage;
    public GetExpListTask(GetExpListListener listener) {
        this.listener = listener;
    }

    public GetExpListTask(GetExpListListener listener, boolean isImage) {
        this.listener = listener;
        this.isImage = isImage;
    }

    @Override
    protected List<Expression> doInBackground(String... strings) {
        String name = strings[0];
        List<Expression> expressionList;
        try {
            if (isImage){
                expressionList = LitePal.where("foldername = ?",name).find(Expression.class);
            }else {
                expressionList = LitePal.select("id","name","foldername","status","url","expressionfolder_id","desstatus","description").where("foldername = ?",name).find(Expression.class);
            }

            sleep(700);
            return expressionList;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    @Override
    protected void onPostExecute(List<Expression> expressions) {
        listener.onFinish(expressions);
    }
}
