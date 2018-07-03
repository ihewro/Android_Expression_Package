package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.web.WebExpressionFolder;
import com.ihewro.android_expression_package.http.HttpUtil;
import com.ihewro.android_expression_package.http.WebImageInterface;
import com.ihewro.android_expression_package.util.UIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ExpFolderDetailActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.owner_name)
    CircleImageView ownerName;


    private int dirId = 0;
    private List<Expression> expressionList = new ArrayList<>();
    private ExpressionListAdapter adapter;

    public static void actionStart(Activity activity,int dir){
        Intent intent = new Intent(activity,ExpFolderDetailActivity.class);
        intent.putExtra("dir",dir);
        activity.startActivityForResult(intent,1);
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

        GridLayoutManager gridLayoutManager =  new GridLayoutManager(this,5);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ExpressionListAdapter(R.layout.item_expression,expressionList);
        recyclerView.setAdapter(adapter);

    }


    private void requestData(){
        Retrofit retrofit = HttpUtil.getRetrofit(10,10,10);
        WebImageInterface request = retrofit.create(WebImageInterface.class);

        Call<List<Expression>> call = request.getDirDetail(dirId,1,10);

        call.enqueue(new Callback<List<Expression>>() {
            @Override
            public void onResponse(Call<List<Expression>> call, Response<List<Expression>> response) {
                if (response.isSuccessful()){
                    Toasty.success(UIUtil.getContext(),"请求成功", Toast.LENGTH_SHORT).show();
                    adapter.setNewData(response.body());
                    refreshLayout.finishRefresh();
                }else {
                    Toasty.success(UIUtil.getContext(),"请求失败", Toast.LENGTH_SHORT).show();
                    refreshLayout.finishRefresh();
                }
            }

            @Override
            public void onFailure(Call<List<Expression>> call, Throwable t) {
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

    }
}
