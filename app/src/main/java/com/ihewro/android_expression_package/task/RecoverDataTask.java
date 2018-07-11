package com.ihewro.android_expression_package.task;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.activity.MainActivity;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.util.DateUtil;
import com.ihewro.android_expression_package.util.FileUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/11
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class RecoverDataTask extends AsyncTask<Void,Void,Boolean> {

    private Activity activity;
    String [] backupFiles;

    public RecoverDataTask(Activity activity) {
        this.activity = activity;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        File dir = new File(GlobalConfig.appDirPath + "database");
        if (!dir.exists() || !dir.isDirectory()){
            dir.mkdir();
            return false;
        }else {
            backupFiles = dir.list();
            return true;
        }
    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        super.onPostExecute(aVoid);
        if (aVoid){
            new MaterialDialog.Builder(activity)
                    .title("备份列表")
                    .items(backupFiles)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View view, final int which, CharSequence text) {
                            new MaterialDialog.Builder(activity)
                                    .title("确认恢复此备份吗？")
                                    .content("一旦恢复数据后，无法撤销操作。但是你可以稍后继续选择恢复其他备份文件")
                                    .positiveText("确定")
                                    .negativeText("取消")
                                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which2) {
                                            ALog.d(GlobalConfig.appDirPath + "database/" + backupFiles[which]);
                                            FileUtil.copyFileToTarget(GlobalConfig.appDirPath + "database/" + backupFiles[which],activity.getDatabasePath("expBaby.db").getAbsolutePath());
                                            Toasty.success(activity,"恢复数据成功", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE));
                                        }
                                    })
                                    .show();
                        }
                    })
                    .show();
        }else {
            Toasty.info(activity,"暂无任何备份文件，请先备份数据",Toast.LENGTH_SHORT).show();
        }
    }
}
