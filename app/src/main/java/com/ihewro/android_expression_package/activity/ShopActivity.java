package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpShopRecyclerViewAdapter;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.ExpressionFolderList;
import com.ihewro.android_expression_package.http.HttpUtil;
import com.ihewro.android_expression_package.http.WebImageInterface;
import com.ihewro.android_expression_package.util.UIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
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

    private int currentPage = 1;
    private int totalCount = 0;

    //适配器
    private ExpShopRecyclerViewAdapter adapter;

    private List<ExpressionFolder> expressionFolderList = new ArrayList<>();

    private Call<ExpressionFolderList> call;
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
    private void requestData(final int page){
        currentPage = page;
        if (currentPage > 1 && (currentPage-1) * 10 > totalCount){
            refreshLayout.finishLoadMoreWithNoMoreData();//没有更多数据了,显示不能加载更多提示
        }else {
            Retrofit retrofit = HttpUtil.getRetrofit(20,20,20);
            WebImageInterface request = retrofit.create(WebImageInterface.class);
            call = request.getDirList(currentPage,10);

            call.enqueue(new Callback<ExpressionFolderList>() {
                @Override
                public void onResponse(Call<ExpressionFolderList> call, final Response<ExpressionFolderList> response) {
                    if (response.isSuccessful()){
                        Toasty.success(UIUtil.getContext(), "请求成功", Toast.LENGTH_SHORT, true).show();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                if (currentPage == 1){
                                    expressionFolderList.clear();
                                }
                                expressionFolderList.addAll(response.body().getExpressionFolderList());

                                ShopActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (currentPage == 1){//上拉刷新
                                            adapter.setNewData(response.body().getExpressionFolderList());
                                            totalCount  = response.body().getCount();
                                            refreshLayout.finishRefresh();
                                        }else {//下拉更多
                                            adapter.addData(response.body().getExpressionFolderList());
                                            refreshLayout.finishLoadMore();
                                        }

                                        currentPage ++ ;
                                    }
                                });
                            }
                        }).start();
                    }else {
                        Toasty.error(ShopActivity.this, "请求失败", Toast.LENGTH_SHORT, true).show();
                        if (page == 1){
                            refreshLayout.finishRefresh(false);
                        }else {
                            refreshLayout.finishLoadMore(false);
                        }
                    }

                }

                @Override
                public void onFailure(Call<ExpressionFolderList> call, Throwable t) {
                    Toasty.info(UIUtil.getContext(), "请求失败或取消请求", Toast.LENGTH_SHORT, true).show();
                    ALog.d(t.toString());
                    if (page == 1){
                        refreshLayout.finishRefresh(false);
                    }else {
                        refreshLayout.finishLoadMore(false);
                    }
                }
            });
        }
    }

    public void initListener() {
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                requestData(1);
            }
        });

        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                requestData(currentPage);
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
        }else if (item.getItemId() == R.id.my_manage){
            MyActivity.actionStart(ShopActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_shop, menu);

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (call!=null){
            call.cancel();
        }
    }
}
