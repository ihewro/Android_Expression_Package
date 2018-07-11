package com.ihewro.android_expression_package.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.animation.BaseAnimation;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpMyRecyclerViewAdapter;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.util.DateUtil;
import com.ihewro.android_expression_package.util.UIUtil;
import com.ihewro.android_expression_package.view.ExpImageDialog;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

public class ResultActivity extends BaseActivity {


    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    View notDataView;
    ExpressionListAdapter adapter;
    List<Expression> expressionList = new ArrayList<>();
    private String searchText;
    private ExpImageDialog expressionDialog;

    public static void actionStart(Activity activity, String searchText) {
        Intent intent = new Intent(activity, ResultActivity.class);
        intent.putExtra("text", searchText);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        ButterKnife.bind(this);

        initData();

        initView();

        getSearchData();


        //点击监听
        adapter.setOnItemClickListener(new ExpressionListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                Expression expression = expressionList.get(position);
                expressionDialog.setImageData(expression);
                expressionDialog.show();
            }
        });

    }

    private void initView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        toolbar.setTitle("搜索：" + searchText);
        notDataView = getLayoutInflater().inflate(R.layout.item_empty_view2, (ViewGroup) recyclerView.getParent(), false);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ExpressionListAdapter(expressionList,false);
        recyclerView.setAdapter(adapter);


        expressionDialog = new ExpImageDialog.Builder(Objects.requireNonNull(this))
                .setContext(this, null)
                .build();
    }

    private void initData() {
        if (getIntent()!=null){
            searchText = getIntent().getStringExtra("text");
        }
    }


    private void getSearchData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Expression> searchExpressionList = LitePal.where("description like ?","%"+searchText+"%").find(Expression.class);
                ResultActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toasty.success(ResultActivity.this,"搜索到" + searchExpressionList.size() + "个结果").show();
                        expressionList = searchExpressionList;
                        adapter.setNewData(searchExpressionList);

                        if (searchExpressionList.size() == 0){
                            adapter.setNewData(null);
                            adapter.setEmptyView(notDataView);
                        }
                    }
                });
            }
        }).start();
    }
}
