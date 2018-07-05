package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpMyRecyclerViewAdapter;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.util.UIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;


    //适配器
    private ExpMyRecyclerViewAdapter adapter;

    private List<ExpressionFolder> expressionFolderList = new ArrayList<>();


    public static void actionStart(Activity activity){
        Intent intent = new Intent(activity,MyActivity.class);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ButterKnife.bind(this);

        initData();

        initView();

        initListener();
    }


    private void initView() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(UIUtil.getContext()));
        adapter = new ExpMyRecyclerViewAdapter(expressionFolderList,this);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        recyclerView.setAdapter(adapter);
    }

    /**
     * 读取数据库的信息，获取本地的图片信息
     */
    private void initData() {
        //查询到所有的表情包目录，但是有的表情包目录status可能是-1，即无效表情包
        //List<ExpressionFolder> databaseExpFolderList =(List<ExpressionFolder>) LitePal.findAll(ExpressionFolder.class);



    }

    /**
     * 监听事件
     */
    private void initListener() {

    }


}
