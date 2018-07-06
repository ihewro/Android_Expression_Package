package com.ihewro.android_expression_package.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.SaveImageToGalleryListener;
import com.ihewro.android_expression_package.task.SaveImageToGalleryTask;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.ShareUtil;
import com.ihewro.android_expression_package.util.ToastUtil;
import com.ihewro.android_expression_package.util.UIUtil;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;

import es.dmoral.toasty.Toasty;
import pl.droidsonroids.gif.GifImageView;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/03
 *     desc   : 在MaterialDialog 基础上封装了一层，用来显示单个表情包
 *     version: 1.0
 * </pre>
 */
public class ExpImageDialog extends MaterialDialog{


    //自定义布局
    private GifImageView ivExpression;
    private TextView tvExpression;
    private View save;
    private View share;
    //View timShare;
    private View weChatShare;
    private View qqShare;//qq分享
    private View love;//输出一句撩人的话

    private final Builder builder;
    private Activity activity;//显示该对话框的活动
    private Fragment fragment;//显示该对话框的碎片

    private Expression expression;


    private String[] loves = new String[]{
            "每次看到你的时候 我都觉得 呀我要流鼻血啦 可是 我从来没留过鼻血 我只会流眼泪",
            "给喜欢的人发撩人表情包吧✨",
            "你可知 你是我青春年少时义无反顾的梦",
            "给喜欢的人发撩人表情包吧✨",
            "请记住我",
            "时间将它磨得退色，又被岁月添上新的柔光，以至于如今的我再已无法辨别当时的心情。那就当是一见钟情吧。",
            "晚来天欲雪，能饮一杯无。",
            "当时明月在，曾照彩云归",
            "都崭新，都暗淡，都独立，都有明天。"
    };



    ExpImageDialog(Builder builder) {
        super(builder);
        this.builder = builder;
        initData();
        initView();//初始化自定义布局
        initListener();//注册监听器
    }

    private void initData(){
        this.activity = this.builder.activity;
        this.fragment = this.builder.fragment;
    }


    /**
     * 获取到最新的控件数据
     * @param expression
     */
    public void setImageData(Expression expression){
        this.expression = expression;
    }

    /**
     * 更新对话框的界面数据
     */
    private void updateUI(){
        if (expression.getStatus() != 2){//不是网络图片，将保存按钮取消掉
            save.setVisibility(View.VISIBLE);
        }
        UIUtil.setImageToImageView(expression.getStatus(),expression.getUrl(),ivExpression);
        tvExpression.setText(expression.getName());
    }

    @Override
    public void show() {
        updateUI();
        super.show();

    }

    private void initView(){

        View view = getCustomView();
        assert view != null;
        ivExpression = view.findViewById(R.id.expression_image);
        tvExpression = view.findViewById(R.id.expression_name);
        save = view.findViewById(R.id.save_image);
        share = view.findViewById(R.id.share);
        //timShare = view.findViewById(R.id.tim_share);
        weChatShare = view.findViewById(R.id.weChat_share);
        qqShare = view.findViewById(R.id.qq_share);
        love = view.findViewById(R.id.love);

    }

    private void initListener(){

        //保存图片到本地
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveImageToGalleryTask(new SaveImageToGalleryListener() {
                    @Override
                    public void onFinish(Boolean result) {
                        if (result){
                            Toasty.success(UIUtil.getContext(),"已保存到" +GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName(), Toast.LENGTH_SHORT).show();
                        }else {
                            Toasty.error(activity,"保存失败，请联系作者",Toast.LENGTH_SHORT).show();
                        }
                    }
                },activity).execute(expression);
            }
        });

        //调用系统分享
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SaveImageToGalleryTask(new SaveImageToGalleryListener() {
                    @Override
                    public void onFinish(Boolean result) {
                        if (result){
                            File filePath = new File(GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName());
                            Log.e("filepath",filePath.getAbsolutePath());
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            Uri imageUri = FileProvider.getUriForFile(
                                    activity,
                                    UIUtil.getContext().getPackageName() + ".fileprovider",
                                    filePath);

                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                            shareIntent.setType("image/*");
                            activity.startActivity(Intent.createChooser(shareIntent, "分享到"));
                        }
                    }
                },activity).execute(expression);

            }
        });

        //调用QQ分享
        qqShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                new SaveImageToGalleryTask(new SaveImageToGalleryListener() {
                    @Override
                    public void onFinish(Boolean result) {
                        if (result){
                            File filePath = new File(GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName());
                            Log.e("filepath", filePath.getAbsolutePath());
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            Uri imageUri = FileProvider.getUriForFile(
                                    activity,
                                    UIUtil.getContext().getPackageName() + ".fileprovider",
                                    filePath);

                            ShareUtil.shareQQFriend("title", "content", ShareUtil.DRAWABLE, imageUri);
                        }
                    }
                },activity).execute(expression);
            }
        });

        //调用微信分享
        weChatShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SaveImageToGalleryTask(new SaveImageToGalleryListener() {
                    @Override
                    public void onFinish(Boolean result) {
                        if (result){
                            File filePath = new File(GlobalConfig.appDirPath + expression.getFolderName() + "/" + expression.getName());
                            Log.e("filepath", filePath.getAbsolutePath());
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            Uri imageUri = FileProvider.getUriForFile(
                                    activity,
                                    UIUtil.getContext().getPackageName() + ".fileprovider",
                                    filePath);

                            ShareUtil.shareWeChatFriend("title","content",ShareUtil.DRAWABLE,imageUri);
                        }
                    }
                },activity).execute(expression);
            }
        });


        //点击爱心
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView)love).setImageDrawable(new IconicsDrawable(activity)
                        .icon(GoogleMaterial.Icon.gmd_favorite)
                        .color(Color.RED)
                        .sizeDp(24));
                int position = (int)(Math.random()*(loves.length - 1));
                ToastUtil.showMessageShort(loves[position]);
            }
        });

    }

    public static class Builder extends MaterialDialog.Builder {

        private Activity activity;//显示该对话框的活动
        private Fragment fragment;//显示该对话框的碎片

        public Builder(@NonNull Context context) {
            super(context);
        }

        public Builder setContext(Activity activity,Fragment fragment){
            this.activity = activity;
            this.fragment = fragment;
            return this;
        }


        @Override
        public ExpImageDialog build() {
            this.customView(R.layout.item_show_expression, true);
            return new ExpImageDialog(this);
        }
    }

}
