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
import android.util.Pair;
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
import com.ihewro.android_expression_package.util.ToastUtil;
import com.ihewro.android_expression_package.util.UIUtil;
import com.ihewro.android_expression_package.view.ExpImageDialog;
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
import java.util.Objects;

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
    private ExpImageDialog expressionDialog;
    private int currentPosition = -1;




    public ExpressionContentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expression_content, container, false);
        unbinder = ButterKnife.bind(this, view);

        //初始化弹出层相关信息
        initView();


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
                expressionDialog.setImageData(expression);
                expressionDialog.show();

            }
        });
    }


    /**
     * 初始化表情包数据
     */
    private void initExpressionData(){
        Bundle bundle = getArguments();
        assert bundle != null;
        expressionList = (List<Expression>) bundle.getSerializable("data");
    }



    private void initView(){
        expressionDialog  = new ExpImageDialog.Builder(Objects.requireNonNull(getActivity()))
                .setContext(getActivity(),this)
                .build();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
