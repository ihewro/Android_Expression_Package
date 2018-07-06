package com.ihewro.android_expression_package.adapter;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.activity.ExpWebFolderDetailActivity;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.http.HttpUtil;
import com.ihewro.android_expression_package.http.WebImageInterface;
import com.ihewro.android_expression_package.util.DateUtil;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ExpShopRecyclerViewAdapter extends BaseQuickAdapter<ExpressionFolder, BaseViewHolder> {
    @BindView(R.id.exp_name)
    TextView expName;
    @BindView(R.id.image_1)
    ImageView image1;
    @BindView(R.id.image_2)
    ImageView image2;
    @BindView(R.id.image_3)
    ImageView image3;
    @BindView(R.id.image_4)
    ImageView image4;
    @BindView(R.id.image_5)
    ImageView image5;
    @BindView(R.id.exp_num)
    TextView expNum;
    @BindView(R.id.owner_name)
    TextView ownerName;

    private Activity activity = null;
    public ExpShopRecyclerViewAdapter(@Nullable List<ExpressionFolder> data, Activity activity) {
        super(R.layout.item_exp_shop, data);
        this.activity = activity;
    }

    private MaterialDialog downloadAllDialog;
    private int downloadCount = 0;//合集已经下载的数目
    private int downloadAllCount;//要下载的合集数目

    List<ExpressionFolder> expressionFolderList = new ArrayList<>();
    private ExpressionFolder expressionFolder;

    @Override
    protected void convert(BaseViewHolder helper, final ExpressionFolder item) {
        helper.setText(R.id.exp_name,item.getName());
        helper.setText(R.id.exp_num,item.getCount() + "+");
        helper.setText(R.id.owner_name,item.getOwner());

        if (item.getName().contains("需要密码") || item.getName().contains("污污污")){
            helper.getView(R.id.download_exp).setVisibility(View.GONE);//先隐藏，答对问题才能显示该按钮
            helper.getView(R.id.item_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zoreChallenge();
                }
            });

        }else {//普通表情包
            int imageViewArray[] = new int[]{R.id.image_1,R.id.image_2,R.id.image_3,R.id.image_4,R.id.image_5};
            ALog.d(item.getExpressionList().size());
            int num = 0;
            if (item.getExpressionList().size()<5){
                num = item.getExpressionList().size();
            }else {
                num = 5;
            }

            for (int i =0;i<num;i++){
                UIUtil.setImageToImageView(2,item.getExpressionList().get(i).getUrl(), (GifImageView) helper.getView(imageViewArray[i]));
            }
            //如果表情包数目小于5，则剩余的表情占位不显示
            for (int j = num;j< 5; j++){
                helper.getView(imageViewArray[j]).setVisibility(View.INVISIBLE);
                helper.getView(R.id.fl_image_5).setVisibility(View.INVISIBLE);
            }

            helper.getView(R.id.item_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExpWebFolderDetailActivity.actionStart(UIUtil.getContext(),item.getDir(),item.getName());
                }
            });

        }

        //下载表情包
        helper.getView(R.id.download_exp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                1. 将文件下载到本地
                2. 下载的图片信息存储到数据库中
                3. 更新图库以便显示出下载的图片
                */
                downloadAllDialog = new MaterialDialog.Builder(activity)
                        .title("正在下载，请稍等")
                        .content("陛下，耐心等下……")
                        .progress(false, item.getCount(), true)
                        .show();

                HttpUtil.getExpressionList(item.getDir(), 1, 99999999,item.getName(), new Callback<List<Expression>>() {//获取该目录下的所有表情包，不分页
                    @Override
                    public void onResponse(Call<List<Expression>> call, Response<List<Expression>> response) {
                        if (response.isSuccessful()){
                            final List<Expression> expFolderAllExpList = response.body();
                            downloadAllCount = expFolderAllExpList.size();
                            Retrofit retrofit = HttpUtil.getRetrofit(10,10,10);
                            WebImageInterface request = retrofit.create(WebImageInterface.class);
                            if (expFolderAllExpList.size()<=0){
                                downloadAllDialog.dismiss();
                            }else {
                                final File dirFile = new File(Environment.getExternalStorageDirectory() + "/" + GlobalConfig.storageFolderName + "/" +item.getName());
                                if (!dirFile.exists()){
                                    dirFile.mkdir();
                                }
                                //数据库中添加目录信息,添加之前需要查询数据库中是否已经存在该表情包，如果存在的话，需要更新

                                //当前目录的持久化对象，这里更新数据不能适用update,否则表情的表的外键无法更新的。url:https://github.com/LitePalFramework/LitePal/issues/282
                                expressionFolder = null;

                                expressionFolderList.clear();;
                                expressionFolderList = LitePal.where("name = ? and exist = ?",item.getName(),"1").find(ExpressionFolder.class);

                                if (expressionFolderList.size()>0){//这里按照我的逻辑，大小肯定是1的，如果不是，就抛出错误提示吧，因为表情包的文件名称肯定是唯一的。

                                    //如果存在的话，需要更新
                                    expressionFolder = expressionFolderList.get(0);
                                    ALog.d(expressionFolder);
                                    expressionFolder.setCount(0);
                                    expressionFolder.setUpdateTime(DateUtil.getNowDateStr());
                                    expressionFolder.save();

                                    //需要删除该目录对应的表情列表，然后再更新，否则就重复了
                                    LitePal.deleteAll(Expression.class,"name = ?", expressionFolder.getName());

                                }else {
                                    expressionFolder = new ExpressionFolder(1,0,item.getName(),item.getOwner(),item.getOwnerAvatar(), DateUtil.getNowDateStr(),null,new ArrayList<Expression>(),item.getDir());
                                    expressionFolder.save();
                                }


                                downloadCount = 0;

                                for (int i = 0;i<expFolderAllExpList.size();i++){
                                    //对每个下载地址都进行进度条的监听
                                    ProgressManager.getInstance().addResponseListener(expFolderAllExpList.get(i).getUrl(), getDownloadListener());
                                    Call<ResponseBody> call2 = request.downloadWebExp(expFolderAllExpList.get(i).getUrl());
                                    final int finalI = i;
                                    call2.enqueue(new Callback<ResponseBody>() {//执行下载
                                        @Override
                                        public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {

                                            try {

                                                File file = new File( dirFile.getAbsoluteFile()  + "/" + expFolderAllExpList.get(finalI).getName());
                                                ALog.d(file.getAbsolutePath());
                                                downloadCount++;

                                                //下载成功的话，将下载的图片信息存到数据库中，并更新对应的目录表
                                                Expression expression = new Expression(1,expFolderAllExpList.get(finalI).getName(),file.getAbsolutePath(),item.getName());
                                                expression.save();

                                                //更新数据中该目录的关联数据
                                                ALog.d("folder", expressionFolder.isSaved() + "" + expressionFolder.getId());
                                                expressionFolder.setCount(downloadCount);
                                                expressionFolder.getExpressionList().add(expression);
                                                expressionFolder.save();

                                                //更新图片库
                                                FileUtil.updateMediaStore(activity,file.getAbsolutePath());

                                                //如果全部下载完成，进度条框提示下载完成。
                                                if (downloadCount >= downloadAllCount){
                                                    downloadAllDialog.setProgress(downloadAllCount);
                                                    downloadAllDialog.setContent("下载完成");
                                                }
                                                assert response.body() != null;
                                                InputStream is = response.body().byteStream();
                                                FileOutputStream fos = new FileOutputStream(file);
                                                byte[] bytes = UIUtil.InputStreamTOByte(is);
                                                fos.write(bytes);
                                                fos.flush();
                                                fos.close();
                                            } catch (java.io.IOException e) {
                                                e.printStackTrace();
                                            }

                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {

                                        }
                                    });

                                }

                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<List<Expression>> call, Throwable t) {

                    }
                });





            }
        });
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
            }

            @Override
            public void onError(long id, Exception e) {

            }
        };
    }

    /**
     * 第0层挑战
     */
    private void zoreChallenge(){
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .title("你确定要进入吗？")
                .content("这里内容可能并不是那么友好")
                .positiveText("当然")
                .negativeText("那我就不看了")
                .cancelable(false)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        firstChallenge();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        detainBeforeExit();
                    }
                })
                .show();
    }

    /**
     * 第1层挑战
     */
    private void firstChallenge(){
        MaterialDialog dialog = new MaterialDialog.Builder(activity)
                .content("但是前提你得成年，告诉你你多大了吧？")
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)
                .input("你的年龄", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        // Do something
                    }
                }).show();
    }

    /**
     * 退出前的挽留
     */
    private void detainBeforeExit(){
        new MaterialDialog.Builder(activity)
                .content("真的不看吗？")
                .positiveText("真的")
                .negativeText("那我就免为其难的看看吧（偷瞄~~( ﹁ ﹁ ) ~~~）")
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        firstChallenge();
                    }
                })
                .cancelable(false)
                .show();
    }
}
