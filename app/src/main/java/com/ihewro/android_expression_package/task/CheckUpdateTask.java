package com.ihewro.android_expression_package.task;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.bean.Version;
import com.ihewro.android_expression_package.http.HttpUtil;
import com.ihewro.android_expression_package.http.WebImageInterface;
import com.ihewro.android_expression_package.util.APKVersionCodeUtils;
import com.ihewro.android_expression_package.util.UIUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import es.dmoral.toasty.Toasty;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static java.lang.Thread.sleep;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/08
 *     desc   : 检查更新
 *     version: 1.0
 * </pre>
 */
public class CheckUpdateTask {
    private  PackageManager packageManager;
    private Activity activity;

    private Version version;
    private MaterialDialog downloadDialog;
    public CheckUpdateTask(Activity activity, PackageManager packageManager) {
        this.activity = activity;
        this.packageManager =  packageManager;
    }


    public boolean execute(){
        final MaterialDialog loading = new MaterialDialog.Builder(activity)
                .content("检查更新中……")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();

        final Retrofit retrofit = HttpUtil.getRetrofit(10,10,10);
        final WebImageInterface request = retrofit.create(WebImageInterface.class);
        Call<Version> call = request.getAndroidLatestVersion(APKVersionCodeUtils.getVersionCode(activity));

        call.enqueue(new Callback<Version>() {
            @Override
            public void onResponse(Call<Version> call, final Response<Version> response) {
                ALog.d("请求成功" + response.body().toString());
                if (response.isSuccessful()){
                    if (response.body().isUpdate()){
                        new MaterialDialog.Builder(activity)
                                .title("是否立即下载最新版本？")
                                .content("更新内容：" + response.body().getUpdateText())
                                .positiveText("我想下载")
                                .negativeText("先算了吧")
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        //下载新的版本
                                        dialog.dismiss();
                                        version = response.body();
                                        downloadApk();
                                    }
                                })
                                .show();
                    }else {
                        //没有更新
                        ALog.d("当前版本已经是最新版本");
                        Toasty.info(activity,"当前版本已经是最新版本", Toast.LENGTH_SHORT).show();
                    }
                }
                loading.dismiss();
            }

            @Override
            public void onFailure(Call<Version> call, Throwable t) {
                ALog.d(t.getMessage());
                loading.dismiss();
            }
        });


        return true;
    }


    private void downloadApk(){

        String filePath = GlobalConfig.appDirPath;
        File dir = new File(filePath);
        if (!dir.exists())dir.mkdir();
        final File apk = new File(filePath+version.getLatestCode()+".apk");//apk文件以versioncode命名
        if (!apk.exists()){//如果本地没有最新版本的apk了，需要下载
            if (version.isAssets()){//判断是否有下载地址
                final Retrofit retrofit = HttpUtil.getRetrofit(10,10,10);
                final WebImageInterface request = retrofit.create(WebImageInterface.class);
                Call<ResponseBody> call1 = request.downloadWebUrl(version.getUrl());
                ProgressManager.getInstance().addResponseListener(version.getUrl(), getDownloadListener());
                downloadDialog = new MaterialDialog.Builder(activity)
                        .title("正在下载最新版本apk")
                        .content("陛下，耐心等下……")
                        .progress(false, 100, true)
                        .show();


                call1.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){//下载成功，把文件写入到本地，然后自动唤醒软件安装
                            //写入文件
                            assert response.body() != null;
                            InputStream is = response.body().byteStream();
                            try {
                                FileOutputStream fos = null;
                                fos = new FileOutputStream(apk);
                                byte[] bytes = UIUtil.InputStreamTOByte(is);
                                fos.write(bytes);
                                fos.flush();
                                fos.close();
                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }
                            installApk();
                        }else {

                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        downloadDialog.dismiss();
                    }
                });

            }else {//没有下载资源，直接跳转到发布页面
                Uri uri = Uri.parse(version.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }
        }else {
            installApk();//直接安装即可
        }


    }


    public void installApk(){
        boolean flag = false;//是否可以按照
        if (Build.VERSION.SDK_INT >= 26) {
            flag = packageManager.canRequestPackageInstalls();
        }else {
            flag = true;
        }

        if (!flag){//请求安装未知应用来源的权限
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES}, 124);
        }else {
            realInstallApk();
        }
    }

    public void realInstallApk(){
        final File file = new File(GlobalConfig.appDirPath + version.getLatestCode()+".apk");//apk文件以versioncode命名

        ALog.d("安装包地址" + file.getAbsolutePath());

        try {
            Runtime.getRuntime().exec("chmod 777 " + file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri =
                    FileProvider.getUriForFile(activity, "com.ihewro.android_expression_package.fileprovider", file);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            ALog.d("启动安装apk");
        }else{
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
        }
        Toasty.info(activity,"正在为您安装最新apk",Toast.LENGTH_LONG).show();
        activity.startActivity(intent);

    }

    /**
     * 下载进度的接口
     * @return
     */
    @NonNull
    private ProgressListener getDownloadListener() {
        return new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                downloadDialog.setProgress(progressInfo.getPercent());
            }

            @Override
            public void onError(long id, Exception e) {

            }
        };
    }

}

