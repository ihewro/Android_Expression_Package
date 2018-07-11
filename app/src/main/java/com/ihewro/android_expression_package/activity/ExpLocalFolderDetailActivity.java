package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.MyDataBase;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.TaskListener;
import com.ihewro.android_expression_package.task.DeleteImageTask;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.view.ExpImageDialog;
import com.ihewro.android_expression_package.view.MyGlideEngine;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;


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
    @BindView(R.id.to_select)
    TextView toSelect;
    @BindView(R.id.exit_select)
    TextView exitSelect;


    private ExpImageDialog expressionDialog;


    private List<Expression> expressionList;
    private ExpressionListAdapter adapter;
    private int dirId;
    private String dirName;
    private int clickPosition;
    /**
     * 是否显示checkbox
     */
    private boolean isShowCheck = false;
    /**
     * 记录选中的checkbox
     */
    private List<String> checkList = new ArrayList<>();
    List<Expression> deleteExpList = new ArrayList<>();
    private String createTime;
    GridLayoutManager gridLayoutManager;

    Comparator<Integer> cmp = new Comparator<Integer>() {
        public int compare(Integer i1, Integer i2) {
            return i2 - i1;
        }
    };

    public static void actionStart(Activity activity, int dirId, String dirName, String createTime) {
        Intent intent = new Intent(activity, ExpLocalFolderDetailActivity.class);
        intent.putExtra("id", dirId);
        intent.putExtra("folderName", dirName);
        intent.putExtra("time", createTime);
        activity.startActivityForResult(intent, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exp_local_folder_detail);
        ButterKnife.bind(this);

        EventBus.getDefault().register(this);

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
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadMore(false);
        gridLayoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ExpressionListAdapter(expressionList, true);
        recyclerView.setAdapter(adapter);

        expressionDialog = new ExpImageDialog.Builder(Objects.requireNonNull(this))
                .setContext(this, null)
                .build();
        downloadTime.setText(createTime);

    }


    private void initData() {

        if (getIntent() != null) {
            dirId = getIntent().getIntExtra("id", 1);
            dirName = getIntent().getStringExtra("folderName");
            createTime = getIntent().getStringExtra("time");
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
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();

    }


    private void initListener() {

        exitSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContraryCheck();
            }
        });
        toSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContraryCheck();
            }
        });

        selectDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行删除操作
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        deleteExpList.clear();
                        Collections.sort(checkList, new Comparator<String>() {
                            @Override
                            public int compare(String o1, String o2) {
                                return Integer.parseInt(o2) - Integer.parseInt(o1);
                            }
                        });
                        for (int i = 0; i < checkList.size(); i++) {
                            deleteExpList.add(expressionList.get(Integer.parseInt(checkList.get(i))));
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Toast.makeText(ExpLocalFolderDetailActivity.this, checkList.toString(), Toast.LENGTH_SHORT).show();
                                new DeleteImageTask(false, deleteExpList, dirName, new TaskListener() {
                                    @Override
                                    public void onFinish(Boolean result) {
                                        Toasty.success(ExpLocalFolderDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                        for (int i = 0; i < checkList.size(); i++) {
                                            adapter.remove(Integer.parseInt(checkList.get(i)));
                                        }
                                        adapter.notifyDataSetChanged();
                                        setContraryCheck();
                                    }
                                }).execute();

                            }
                        });
                    }
                }).start();
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
                clickPosition = position;
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
                setContraryCheck();
                return false;
            }
        });
    }

    /**
     * 让所有的表情都在选中的状态
     */
    private void setAdapterAllSelected() {
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
    private void setAdapterAllNotSelected() {
        selectAll.setText("全选");
        selectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAdapterAllSelected();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isShowCheck) {
            setContraryCheck();
        } else {
            finish();
        }
    }

    public void setContraryCheck() {
        if (isShowCheck) {//取消批量
            selectDelete.setVisibility(View.GONE);
            adapter.setShowCheckBox(false);
            adapter.notifyDataSetChanged();
            checkList.clear();
        } else {//显示批量
            adapter.setShowCheckBox(true);
            adapter.notifyDataSetChanged();
            selectDelete.setVisibility(View.VISIBLE);
        }
        isShowCheck = !isShowCheck;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUI(final EventMessage eventBusMessage) {
        if (Objects.equals(eventBusMessage.getType(), EventMessage.DESCRIPTION_SAVE)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ALog.d("更新布局" + clickPosition);
                    ALog.d(eventBusMessage.toString());
                    View view = gridLayoutManager.findViewByPosition(clickPosition).findViewById(R.id.notice);
                    ALog.d(view);
                    view.setVisibility(View.GONE);
                    expressionList.get(clickPosition).setDesStatus(1);
                    expressionList.get(clickPosition).setDescription(eventBusMessage.getMessage());
                    EventBus.getDefault().post(new EventMessage(EventMessage.LOCAL_DESCRIPTION_SAVE,eventBusMessage.getMessage(),eventBusMessage.getMessage2(),String.valueOf(clickPosition)));

                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_local_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.re_add) {
            //
            Matisse.from(ExpLocalFolderDetailActivity.this)
                    .choose(MimeType.ofAll(), false)
                    .countable(true)
                    .maxSelectable(9)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .theme(R.style.Matisse_Dracula)
                    .imageEngine(new MyGlideEngine())
                    .forResult(1998);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1998) {
            //把图片加入到图库中
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (data!=null){
                        List<String> addExpList = Matisse.obtainPathResult(data);
                        for (int i = 0; i < addExpList.size(); i++) {
                            File tempFile = new File(addExpList.get(i));
                            String fileName = tempFile.getName();
                            //保存之前先查看数据库中是否已经有了
                            Expression expression = new Expression(1, fileName, "", dirName);
                            MyDataBase.addExpressionRecord(expression,tempFile);
                        }
                        EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE));
                        initData();
                    }

                }
            }).start();
        }
    }
}
