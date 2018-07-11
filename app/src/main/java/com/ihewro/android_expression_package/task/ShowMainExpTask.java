package com.ihewro.android_expression_package.task;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;

import com.blankj.ALog;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.ShowMainExpListener;
import com.ihewro.android_expression_package.fragment.ExpressionContentFragment;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ShowMainExpTask extends AsyncTask<Void,Void,Void> {

    ShowMainExpListener listener;

    List<ExpressionFolder> expressionFolderList = new ArrayList<>();
    List<String> pageTitleList = new ArrayList<>();
    List<Fragment> fragmentList = new ArrayList<>();

    public ShowMainExpTask(ShowMainExpListener listener) {
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        //从数据库中获取表情包信息
        expressionFolderList = LitePal.findAll(ExpressionFolder.class);

        if (expressionFolderList.size() == 0) {//如果没有表情包目录，则会显示为空
            fragmentList.add(ExpressionContentFragment.fragmentInstant("默认",true));
            pageTitleList.add("默认");
        } else {
            for (int i = 0; i < expressionFolderList.size(); i++) {
                fragmentList.add(ExpressionContentFragment.fragmentInstant(expressionFolderList.get(i).getName(),false));
                pageTitleList.add(expressionFolderList.get(i).getName());
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onFinish(fragmentList,pageTitleList);
    }
}
