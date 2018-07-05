package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.view.ExpImageDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 显示本地表情包一个合集
 */
public class ExpLocalFolderDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.download_time_tip)
    TextView downloadTimeTip;
    @BindView(R.id.download_time)
    TextView downloadTime;



    private ExpImageDialog expressionDialog;


    private List<Expression> expressionList;
    private ExpressionListAdapter adapter;
    private int dirId;

    public static void actionStart(Activity activity,int dirId){
        Intent intent = new Intent(activity,ExpLocalFolderDetailActivity.class);
        intent.putExtra("name",dirId);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_local_folder_detail);
        ButterKnife.bind(this);

        initView();

        initData();

        initListener();
    }

    private void initView(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        GridLayoutManager gridLayoutManager =  new GridLayoutManager(this,3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ExpressionListAdapter(R.layout.item_expression,expressionList);
        recyclerView.setAdapter(adapter);

        expressionDialog  = new ExpImageDialog.Builder(Objects.requireNonNull(this))
                .setContext(this,null)
                .build();

    }


    private void initData(){

        if (getIntent()!=null){
            dirId = getIntent().getIntExtra("name",1);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                expressionList = LitePal.where("expressionfolder_id = ?", String.valueOf(dirId)).find(Expression.class);
                ALog.d("输出大小" + expressionList.size());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setNewData(expressionList);
                    }
                });
            }
        }).start();

    }



    private void initListener() {
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Expression expression = expressionList.get(position);
                expressionDialog.setImageData(expression);
                expressionDialog.show();
            }
        });
    }
}
