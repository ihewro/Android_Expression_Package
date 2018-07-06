package com.ihewro.android_expression_package.task;

import android.os.AsyncTask;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.GetExpFolderDataListener;

import org.litepal.LitePal;

import java.util.List;

import static java.lang.Thread.sleep;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GetExpFolderDataTask extends AsyncTask<Void,Integer,String> {

    GetExpFolderDataListener listener;

    public GetExpFolderDataTask(GetExpFolderDataListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(Void... voids) {
        List<ExpressionFolder> expressionFolderList = LitePal.findAll(ExpressionFolder.class,true);
        ObjectMapper mapper = new ObjectMapper();
        try {
            sleep(1000);
            return mapper.writeValueAsString(expressionFolderList);
        } catch (JsonProcessingException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        listener.onFinish(s);
    }
}
