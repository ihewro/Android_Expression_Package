package com.ihewro.android_expression_package.adapter;

import android.app.Activity;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ihewro.android_expression_package.task.DownloadImageTask;
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
import es.dmoral.toasty.Toasty;
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
                    ExpWebFolderDetailActivity.actionStart(activity,item.getDir(),item.getName(),item.getCount());
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

                Toasty.info(activity,"正在获取表情包数据",Toast.LENGTH_SHORT).show();

                HttpUtil.getExpressionList(item.getDir(), 1, 99999999,item.getName(), new Callback<List<Expression>>() {//获取该目录下的所有表情包，不分页
                    @Override
                    public void onResponse(Call<List<Expression>> call, Response<List<Expression>> response) {
                        if (response.isSuccessful()){
                            Toasty.success(activity,"获取表情包数据成功",Toast.LENGTH_SHORT).show();
                            DownloadImageTask task = new DownloadImageTask(response.body(),item.getName(),item.getCount(),activity);
                            task.execute();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Expression>> call, Throwable t) {
                        //获取表情包合集失败，终止下载
                        Toasty.error(activity,"获取表情包数据失败",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
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
