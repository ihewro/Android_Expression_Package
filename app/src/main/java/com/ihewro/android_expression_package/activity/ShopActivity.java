package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpShopRecyclerViewAdapter;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.web.WebExpressionFolder;
import com.ihewro.android_expression_package.bean.web.WebExpressionFolderList;
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
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShopActivity extends AppCompatActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    //适配器
    private ExpShopRecyclerViewAdapter adapter;

    private List<WebExpressionFolder> expressionFolderList = new ArrayList<>();

    public static void actionStart(Activity context) {
        Intent intent = new Intent(context, ShopActivity.class);
        context.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        ButterKnife.bind(this);

        initView();

        initListener();

        refreshLayout.autoRefresh();
    }

    private void initView() {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        //设置RecyclerView
        /*for (int i =0;i<10;i++){
            expressionFolderList.add(new Expression());
        }*/
        recyclerView.setLayoutManager(new LinearLayoutManager(UIUtil.getContext()));
        adapter = new ExpShopRecyclerViewAdapter(expressionFolderList,this);
        adapter.openLoadAnimation(BaseQuickAdapter.SLIDEIN_LEFT);
        recyclerView.setAdapter(adapter);
    }


    private void initData() {

    }

    /**
     * 请求数据
     */
    private void requestData(){

        Retrofit retrofit = HttpUtil.getRetrofit(10,10,10);
        WebImageInterface request = retrofit.create(WebImageInterface.class);
        Call<WebExpressionFolderList> call = request.getDirList();

        call.enqueue(new Callback<WebExpressionFolderList>() {
            @Override
            public void onResponse(Call<WebExpressionFolderList> call, Response<WebExpressionFolderList> response) {
                if (response.isSuccessful()){
                    Toasty.success(UIUtil.getContext(), "请求成功", Toast.LENGTH_SHORT, true).show();
                    adapter.setNewData(response.body().getWebExpressionFolderList());
                    refreshLayout.finishRefresh();
                }else {
                    Toasty.error(UIUtil.getContext(), "请求失败", Toast.LENGTH_SHORT, true).show();
                    refreshLayout.finishRefresh();
                }

            }

            @Override
            public void onFailure(Call<WebExpressionFolderList> call, Throwable t) {
                Toasty.error(UIUtil.getContext(), "请求失败", Toast.LENGTH_SHORT, true).show();
                ALog.d(t.toString());
                refreshLayout.finishRefresh();
            }
        });
    }

    public void initListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestData();
            }
        });
    }

    /**
     * 点击toolbar上的按钮事件
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
