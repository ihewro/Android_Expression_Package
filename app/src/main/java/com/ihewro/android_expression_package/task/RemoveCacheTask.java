package com.ihewro.android_expression_package.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.android_expression_package.activity.MyActivity;
import com.ihewro.android_expression_package.callback.RemoveCacheListener;
import com.ihewro.android_expression_package.util.DataCleanManager;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/07
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RemoveCacheTask extends AsyncTask<Void,Void,Void> {

    private Activity activity;
    private RemoveCacheListener listener;

    MaterialDialog dialog;
    public RemoveCacheTask(Activity activity,RemoveCacheListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        dialog = new MaterialDialog.Builder(activity)
                .title("正在清空缓存")
                .content("陛下，耐心等下……（同步过程）")
                .progress(true, 0)
                .cancelable(false)
                .show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        DataCleanManager.cleanInternalCache(activity);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        dialog.dismiss();
        Toasty.success(activity,"清除缓存成功", Toast.LENGTH_SHORT).show();
        listener.onFinish();
    }
}
