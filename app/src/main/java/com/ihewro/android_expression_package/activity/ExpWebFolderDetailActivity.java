package com.ihewro.android_expression_package.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.http.HttpUtil;
import com.ihewro.android_expression_package.util.UIUtil;
import com.ihewro.android_expression_package.view.ExpImageDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 显示网络的一个表情包合集
 */
public class ExpWebFolderDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.owner_name)
    CircleImageView ownerName;
    private ExpImageDialog expressionDialog;


    private int dirId = 0;
    private List<Expression> expressionList = new ArrayList<>();
    private ExpressionListAdapter adapter;
    int currentPosition = 0;

    public static void actionStart(Context activity, int dir){
        Intent intent = new Intent(activity,ExpWebFolderDetailActivity.class);
        intent.putExtra("dir",dir);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_folder_detail);
        ButterKnife.bind(this);

        initData();

        initView();

        initListener();

        refreshLayout.autoRefresh();
    }


    private void initData() {
        if (getIntent()!=null){
            dirId = getIntent().getIntExtra("dir",0);
        }
    }


    private void initView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        GridLayoutManager gridLayoutManager =  new GridLayoutManager(this,4);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ExpressionListAdapter(R.layout.item_expression,expressionList);
        recyclerView.setAdapter(adapter);

        expressionDialog  = new ExpImageDialog.Builder(Objects.requireNonNull(this))
                .setContext(this,null)
                .build();
    }


    private void requestData(){

        HttpUtil.getExpressionList(dirId,1,50, new Callback<List<Expression>>() {
            @Override
            public void onResponse(@NonNull Call<List<Expression>> call, @NonNull Response<List<Expression>> response) {
                if (response.isSuccessful()){
                    Toasty.success(UIUtil.getContext(),"请求成功", Toast.LENGTH_SHORT).show();
                    expressionList = response.body();
                    adapter.setNewData(response.body());
                    refreshLayout.finishRefresh();
                }else {
                    Toasty.success(UIUtil.getContext(),"请求失败", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishRefresh();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Expression>> call, @NonNull Throwable t) {
                Toasty.success(UIUtil.getContext(),"请求失败", Toast.LENGTH_SHORT).show();
                refreshLayout.finishRefresh();
            }
        });
    }

    private void initListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                requestData();
            }
        });
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
}
