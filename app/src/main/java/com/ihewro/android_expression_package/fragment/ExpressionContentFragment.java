package com.ihewro.android_expression_package.fragment;


import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.ShareUtil;
import com.ihewro.android_expression_package.util.UIUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.http.Url;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpressionContentFragment extends Fragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private ExpressionListAdapter adapter;
    private List<Expression> expressionList = new ArrayList<>();
    private String tabName;
    private MaterialDialog expressionDialog;
    private int currentPosition = -1;

    //自定义布局
    GifImageView ivExpression;
    TextView tvExpression;
    View save;
    View share;
    //View timShare;
    View weChatShare;
    View qqShare;
    View love;

    private String[] loves = new String[]{
            "每次看到你的时候 我都觉得 呀我要流鼻血啦 可是 我从来没留过鼻血 我只会流眼泪",
            "给喜欢的人发撩人表情包吧✨"

    };


    public ExpressionContentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expression_content, container, false);
        unbinder = ButterKnife.bind(this, view);

        //初始化弹出层相关信息
        initView();

        //初始化弹出层的相关点击事件
        initExpressionDialogListener();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GridLayoutManager gridLayoutManager =  new GridLayoutManager(getActivity(),5);
        recyclerView.setLayoutManager(gridLayoutManager);

        initExpressionData();

        adapter = new ExpressionListAdapter(R.layout.item_expression,expressionList);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                currentPosition = position;
                Expression expression = expressionList.get(position);
                UIUtil.setImageToImageView(expression.getStatus(),expression.getUrl(),ivExpression);
                tvExpression.setText(expression.getName());
                expressionDialog.show();

            }
        });
    }


    /**
     * 初始化表情弹出框监听器
     */
    private void initExpressionDialogListener(){

        //保存图片到本地
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存图片到sd卡
                if (currentPosition != -1){
                    FileUtil.saveImageToGallery(UIUtil.getContext(), null,expressionList.get(currentPosition).getUrl(),tabName,expressionList.get(currentPosition).getName(),1);
                }
            }
        });

        //调用系统分享
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File filePath;
                if (currentPosition != -1){
                    filePath = FileUtil.saveImageToGallery(UIUtil.getContext(), null,expressionList.get(currentPosition).getUrl(),tabName,expressionList.get(currentPosition).getName(),2);
                    Log.e("filepath",filePath.getAbsolutePath());
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    Uri imageUri = FileProvider.getUriForFile(
                            getActivity(),
                            UIUtil.getContext().getPackageName() + ".fileprovider",
                            filePath);

                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                    startActivity(Intent.createChooser(shareIntent, "分享到"));
                }

            }
        });

        //调用QQ分享
        qqShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File filePath = FileUtil.saveImageToGallery(UIUtil.getContext(), null,expressionList.get(currentPosition).getUrl(),tabName,expressionList.get(currentPosition).getName(),2);
                Uri imageUri = FileProvider.getUriForFile(
                        getActivity(),
                        UIUtil.getContext().getPackageName() + ".fileprovider",
                        filePath);


                ShareUtil.shareQQFriend("title","content",ShareUtil.DRAWABLE,imageUri);
            }
        });

        //调用微信分享
        weChatShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File filePath = FileUtil.saveImageToGallery(UIUtil.getContext(), null,expressionList.get(currentPosition).getUrl(),tabName,expressionList.get(currentPosition).getName(),2);
                Uri imageUri = FileProvider.getUriForFile(
                        getActivity(),
                        UIUtil.getContext().getPackageName() + ".fileprovider",
                        filePath);


                ShareUtil.shareWeChatFriend("title","content",ShareUtil.DRAWABLE,imageUri);
            }
        });

        //点击爱心
        love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImageView)love).setImageDrawable(new IconicsDrawable(getActivity())
                        .icon(GoogleMaterial.Icon.gmd_favorite)
                        .color(Color.RED)
                        .sizeDp(24));
                int position = (int)(Math.random()*(loves.length - 1));
                Toast.makeText(UIUtil.getContext(),loves[position],Toast.LENGTH_SHORT).show();
            }
        });


    }

    /**
     * 初始化表情包数据
     */
    private void initExpressionData(){
        Bundle bundle = getArguments();
        expressionList = (List<Expression>) bundle.getSerializable("data");
        tabName = bundle.getString("name");
    }



    private void initView(){
        expressionDialog  = new MaterialDialog.Builder(getActivity())
                .customView(R.layout.item_show_expression, true)
                .build();

        View view = expressionDialog.getCustomView();
        ivExpression = view.findViewById(R.id.expression_image);
        tvExpression = view.findViewById(R.id.expression_name);
        save = view.findViewById(R.id.save_image);
        share = view.findViewById(R.id.share);
        //timShare = view.findViewById(R.id.tim_share);
        weChatShare = view.findViewById(R.id.weChat_share);
        qqShare = view.findViewById(R.id.qq_share);
        love = view.findViewById(R.id.love);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
