package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpMyRecyclerViewAdapter;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.UpdateDatabaseListener;
import com.ihewro.android_expression_package.task.UpdateDatabaseTask;
import com.ihewro.android_expression_package.util.UIUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class MyActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;

    View notDataView;

    //适配器
    private ExpMyRecyclerViewAdapter adapter;

    private List<ExpressionFolder> expressionFolderList = new ArrayList<>();


    public static void actionStart(Activity activity){
        Intent intent = new Intent(activity,MyActivity.class);
        activity.startActivityForResult(intent,2);
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
        notDataView = getLayoutInflater().inflate(R.layout.item_empty_view, (ViewGroup) recyclerView.getParent(), false);
        refreshLayout.setEnableLoadMore(false);
        refreshLayout.setEnableRefresh(false);
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<ExpressionFolder> expressionFolderList = LitePal.findAll(ExpressionFolder.class,true);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ALog.d("listSize",expressionFolderList.size());
                        if (expressionFolderList.size() == 0){
                            adapter.setNewData(null);
                            adapter.setEmptyView(notDataView);
                        }else {
                            adapter.setNewData(expressionFolderList);
                        }
                    }
                });
            }
        }).start();


    }

    /**
     * 监听事件
     */
    private void initListener() {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_my, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.re_update){
            //重新同步数据库
            new MaterialDialog.Builder(this)
                    .title("操作通知")
                    .content("您确定需要重新同步数据吗？一般本地表情包数据显示不正常才需要执行此操作。\n并且执行此操作会丢失表情包作者的头像和名称（不影响具体使用）。")
                    .positiveText("朕确定")
                    .negativeText("我只是点着玩的，快关掉快关掉！")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            UpdateDatabaseTask task = new UpdateDatabaseTask(new UpdateDatabaseListener() {

                                private MaterialDialog updateLoadingDialog;

                                @Override
                                public void onFinished() {
                                    updateLoadingDialog.setContent("终于同步完成");
                                    Toasty.success(MyActivity.this,"同步完成", Toast.LENGTH_SHORT).show();
                                    //更新RecyclerView 布局
                                    initData();

                                }

                                @Override
                                public void onProgress(int progress,int max) {
                                    if (max > 0){
                                        if (!updateLoadingDialog.isShowing()){
                                            updateLoadingDialog.setMaxProgress(max);
                                            updateLoadingDialog.show();
                                            ALog.d("有点问题");
                                        }

                                        if (progress > 0){
                                            updateLoadingDialog.setProgress(progress);
                                        }

                                    }
                                }

                                @Override
                                public void onStart() {
                                    updateLoadingDialog = new MaterialDialog.Builder(MyActivity.this)
                                            .title("正在同步信息")
                                            .content("陛下，耐心等下……（同步过程）")
                                            .progress(false, 0, true)
                                            .build();

                                }
                            });
                            task.execute();
                        }
                    })
                    .show();

        }else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
