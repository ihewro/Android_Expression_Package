package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.view.ExpImageDialog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

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
    @BindView(R.id.select_all)
    TextView selectAll;
    @BindView(R.id.select_delete_button)
    TextView selectDeleteButton;
    @BindView(R.id.select_delete)
    RelativeLayout selectDelete;


    private ExpImageDialog expressionDialog;


    private List<Expression> expressionList;
    private ExpressionListAdapter adapter;
    private int dirId;
    private String dirName;

    /**
     * 是否显示checkbox
     */
    private boolean isShowCheck = false;
    /**
     * 记录选中的checkbox
     */
    private List<String> checkList = new ArrayList<>();

    public static void actionStart(Activity activity, int dirId, String dirName) {
        Intent intent = new Intent(activity, ExpLocalFolderDetailActivity.class);
        intent.putExtra("id", dirId);
        intent.putExtra("folderName", dirName);
        activity.startActivityForResult(intent,1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_local_folder_detail);
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

        toolbar.setTitle(dirName);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ExpressionListAdapter(R.layout.item_expression, expressionList);
        recyclerView.setAdapter(adapter);

        expressionDialog = new ExpImageDialog.Builder(Objects.requireNonNull(this))
                .setContext(this, null)
                .build();

    }


    private void initData() {

        if (getIntent() != null) {
            dirId = getIntent().getIntExtra("id", 1);
            dirName = getIntent().getStringExtra("folderName");
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


        selectDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行下载操作
                Toast.makeText(ExpLocalFolderDetailActivity.this, checkList.toString(), Toast.LENGTH_SHORT).show();

            }
        });

        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapterAllSelected();
                selectAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAdapterAllNotSelected();
                    }
                });
            }
        });


        //点击监听
        adapter.setOnItemClickListener(new ExpressionListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {

                if (isShowCheck) {//如果是在多选的状态
                    CheckBox checkBox = view.findViewById(R.id.cb_item);
                    checkBox.setChecked(!checkBox.isChecked());//多选项设置为相反的状态

                    if (checkList.contains(String.valueOf(position))) {
                        checkList.remove(String.valueOf(position));
                    } else {
                        checkList.add(String.valueOf(position));
                    }
                } else {
                    Expression expression = expressionList.get(position);
                    expressionDialog.setImageData(expression);
                    expressionDialog.show();
                }
            }
        });
        //长按监听
        adapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, int position) {
                adapter = ((ExpressionListAdapter) adapter);
                if (isShowCheck) {
                    selectDelete.setVisibility(View.GONE);
                    ((ExpressionListAdapter) adapter).setShowCheckBox(false);
                    adapter.notifyDataSetChanged();
                    checkList.clear();
                } else {
                    ((ExpressionListAdapter) adapter).setShowCheckBox(true);
                    adapter.notifyDataSetChanged();
                    selectDelete.setVisibility(View.VISIBLE);
                }
                isShowCheck = !isShowCheck;
                return false;
            }
        });
    }

    /**
     * 让所有的表情都在选中的状态
     */
    private void setAdapterAllSelected(){
        //选中所有的表情
        adapter.setAllCheckboxNotSelected();
        selectAll.setText("取消全选");
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapterAllNotSelected();
            }
        });
    }

    /**
     * 取消所有表情的选中状态
     */
    private void setAdapterAllNotSelected(){
        selectAll.setText("全选");
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapterAllSelected();
            }
        });
    }
}
