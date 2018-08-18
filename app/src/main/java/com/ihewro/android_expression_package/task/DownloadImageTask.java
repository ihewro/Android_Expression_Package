package com.ihewro.android_expression_package.task;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.http.HttpUtil;
import com.ihewro.android_expression_package.http.WebImageInterface;
import com.ihewro.android_expression_package.util.DateUtil;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
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
 *     time   : 2018/07/05
 *     desc   : 对下载图片进行封装
 *     version: 1.0
 * </pre>
 */
public class DownloadImageTask  {

     /*
      1. 将文件下载到本地
      2. 下载的图片信息存储到数据库中
      3. 更新图库以便显示出下载的图片
     */
     private MaterialDialog downloadAllDialog;
    private int downloadCount = 0;//合集已经下载的数目
    private int downloadAllCount;//要下载的合集数目
    private List<ExpressionFolder> expressionFolderList = new ArrayList<>();
    private ExpressionFolder expressionFolder;

    private List<Expression> expFolderAllExpList;
    private String folderName;
    private int count;
    private Activity activity;

    private List<String> folderNameList = new ArrayList<>();

    private Boolean isReady = false;//是否表情包目录已经加载完毕
    private boolean isExistInFolder = false;//需要下载的当前图片是否存在目录中
    private boolean isStopDown = false;//是否停止下载
    public DownloadImageTask() {

    }

    public DownloadImageTask(List<Expression> expFolderAllExpList, String folderName, int count, Activity activity) {
        this.expFolderAllExpList = expFolderAllExpList;
        this.folderName = folderName;
        this.count = count;
        this.activity = activity;

        init();

    }

    private void init(){
        downloadAllDialog = new MaterialDialog.Builder(activity)
                .title("正在下载，请稍等")
                .content("陛下，耐心等下……")
                .progress(false, count, true)
                .build();
    }

    public void execute(){

        downloadAllDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                ALog.d("下载进行中的对话框取消");
                isStopDown = true;
            }
        });

        //显示选择表情包列表的对话框
        final MaterialDialog expFolderDialog = new MaterialDialog.Builder(activity)
                .title("添加到表情包")
                .content("加载表情目录中稍等")
                .items()
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if (isReady){
                            if (which != 0){
                                folderName = (String) dialog.getItems().get(which);
                            }
                            ALog.d(folderName);
                            download();
                        }

                    }
                })
                .show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //获取表情包目录
                //第一行显示默认的表情包名称，下面显示数据库已经存在表情包名称
                folderNameList.clear();
                folderNameList.add(folderName + "(默认)");
                expressionFolderList = LitePal.select("name").order("ordervalue").find(ExpressionFolder.class);
                for (ExpressionFolder expFolder:
                     expressionFolderList) {
                    folderNameList.add(expFolder.getName());
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ALog.d("主线程");
                        int size = folderNameList.size();
                        expFolderDialog.setContent("单击添加到指定的表情包下");
                        expFolderDialog.setItems(folderNameList.toArray(new String[size]));
                        expFolderDialog.notifyItemsChanged();
                        isReady = true;
                    }
                });
            }
        }).start();
    }



    private void download(){
        downloadAllDialog.show();

        downloadAllCount = expFolderAllExpList.size();
        Retrofit retrofit = HttpUtil.getRetrofit(10,10,10);
        WebImageInterface request = retrofit.create(WebImageInterface.class);
        if (expFolderAllExpList.size()<=0){
            downloadAllDialog.dismiss();
        }else {
            final File dirFile = new File(Environment.getExternalStorageDirectory() + "/" + GlobalConfig.storageFolderName + "/" +folderName);
            if (!dirFile.exists()){//如果目录不存在，先创建一个目录
                dirFile.mkdir();
            }
            //数据库中添加目录信息,添加之前需要查询数据库中是否已经存在该表情包，如果存在的话，需要更新

            //当前目录的持久化对象，这里更新数据不能适用update,否则表情的表的外键无法更新的。url:https://github.com/LitePalFramework/LitePal/issues/282
            expressionFolder = null;

            expressionFolderList.clear();;
            expressionFolderList = LitePal.where("name = ? and exist = ?",folderName,"1").find(ExpressionFolder.class,true);

            if (expressionFolderList.size()>0){//这里按照我的逻辑，大小肯定是1的，如果不是，就抛出错误提示吧，因为表情包的文件名称肯定是唯一的。

                //如果存在的话，需要更新
                expressionFolder = expressionFolderList.get(0);
                ALog.d(expressionFolder);
                //expressionFolder.setCount(0);
                expressionFolder.setUpdateTime(DateUtil.getNowDateStr());
                expressionFolder.save();

                //需要删除该目录对应的表情列表，然后再更新，否则就重复了
                //LitePal.deleteAll(Expression.class,"foldername = ?", expressionFolder.getName());

            }else {
                expressionFolder = new ExpressionFolder(1,0,folderName,null,null, DateUtil.getNowDateStr(),null,new ArrayList<Expression>(),-1);
                expressionFolder.save();
            }


            downloadCount = 0;

            for (int i = 0;i<expFolderAllExpList.size();i++){
                //对每个下载地址都进行进度条的监听
                if (!isStopDown){
                    ProgressManager.getInstance().addResponseListener(expFolderAllExpList.get(i).getUrl(), getDownloadListener());
                    Call<ResponseBody> call2 = request.downloadWebUrl(expFolderAllExpList.get(i).getUrl());
                    final int finalI = i;
                    call2.enqueue(new Callback<ResponseBody>() {//执行下载
                        @Override
                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                            downloadCount++;

                            try {
                                //写入文件进行压缩，然后再写成bytes 存到数据库中
                                assert response.body() != null;
                                byte[] bytes = response.body().bytes();
                                File temFile = new File(GlobalConfig.appDirPath + expFolderAllExpList.get(finalI).getName());
                                FileUtil.bytesSavedToFile(bytes,temFile);

                                File compressTempFile = FileUtil.returnCompressExp(temFile);
                                bytes = FileUtil.fileToBytes(compressTempFile);
                                if (temFile.exists()){
                                    temFile.delete();
                                }
                                if (compressTempFile.exists()){
                                    compressTempFile.delete();
                                }

                                if (bytes.length > 2060826){
                                    Toasty.info(activity,expFolderAllExpList.get(finalI).getName() +"大小太大，将不会存储").show();
                                }else {
//检查数据库里面有没有这个表情的信息，如果有的话，就不用修改数据库信息了
                                    isExistInFolder = false;
                                    List<Expression> temp = LitePal.select("id","name","foldername","status","url","expressionfolder_id","desstatus","description").where("name = ? and foldername = ?",expFolderAllExpList.get(finalI).getName(),folderName).find(Expression.class);
                                    if (temp.size()>0){//找到记录了
                                        isExistInFolder = true;
                                    }

                                    if (!isExistInFolder){//目录表没有这个表情数据，则数目加1，下载成功的话，将下载的图片信息存到数据库中，并更新对应的目录表
                                        Expression expression = new Expression(1,expFolderAllExpList.get(finalI).getName(),expFolderAllExpList.get(finalI).getUrl(),folderName,expressionFolder,bytes);
                                        new GetExpDesTask(activity,false).execute(expression);
                                        expression.save();
                                        //更新数据中该目录的关联数据
                                        ALog.d("folder233", expressionFolder.isSaved() + "" + expressionFolder.getId());
                                        expressionFolder.setCount(expressionFolder.getCount() + 1);
                                        expressionFolder.getExpressionList().add(expression);
                                        expressionFolder.save();
                                    }
                                }


                                //如果全部下载完成，进度条框提示下载完成。
                                if (downloadCount >= downloadAllCount){
                                    downloadAllDialog.setProgress(downloadAllCount);
//                                    downloadAllDialog.setContent("下载完成");
                                    downloadAllDialog.dismiss();
                                    Toasty.success(activity,"下载完成").show();
                                    UIUtil.autoBackUpWhenItIsNecessary();
                                    EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE));
                                }

                                //删除temp文件夹，因为之前识别文字产生了图片
                                FileUtil.delFolder(GlobalConfig.appTempDirPath);

                            } catch (java.io.IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            //一般情况下是不可能下载失败的
                            //某个文件下载失败
                            Toasty.error(activity,expFolderAllExpList.get(finalI).getName() +"文件下载失败", Toast.LENGTH_SHORT).show();
                            downloadCount++;//同样也需要加一，否则进度条就不对了
                        }
                    });

                }else {//TODO: 这个中止下载好像不起作用
                    downloadAllDialog.dismiss();
                    Toasty.info(activity,"已经中止下载",Toast.LENGTH_SHORT).show();
                    break;
                }

            }
        }
    }

    /**
     * 下载进度时间接口
     * @return
     */
    @NonNull
    private ProgressListener getDownloadListener() {
        return new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {


                double temp = (downloadCount * 100 + progressInfo.getPercent()*1.0)/(downloadAllCount *100);
                downloadAllDialog.setProgress((int) ((temp) * (downloadAllCount -1)));
                downloadAllDialog.show();
            }

            @Override
            public void onError(long id, Exception e) {

            }
        };
    }


    public List<ExpressionFolder> getExpressionFolderList() {
        return expressionFolderList;
    }

    public void setExpressionFolderList(List<ExpressionFolder> expressionFolderList) {
        this.expressionFolderList = expressionFolderList;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
