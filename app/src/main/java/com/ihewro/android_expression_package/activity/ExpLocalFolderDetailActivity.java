package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.MyDataBase;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.callback.GetExpListListener;
import com.ihewro.android_expression_package.callback.SaveImageToGalleryListener;
import com.ihewro.android_expression_package.callback.TaskListener;
import com.ihewro.android_expression_package.task.DeleteExpFolderTask;
import com.ihewro.android_expression_package.task.DeleteImageTask;
import com.ihewro.android_expression_package.task.EditExpFolderNameTask;
import com.ihewro.android_expression_package.task.GetExpListTask;
import com.ihewro.android_expression_package.task.MoveExpTask;
import com.ihewro.android_expression_package.task.SaveFolderToLocalTask;
import com.ihewro.android_expression_package.task.ShowAllExpFolderTask;
import com.ihewro.android_expression_package.util.UIUtil;
import com.ihewro.android_expression_package.view.ExpImageDialog;
import com.ihewro.android_expression_package.view.MyGlideEngine;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
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
    @BindView(R.id.to_move)
    TextView toMove;


    private ExpImageDialog expressionDialog;


    private List<Expression> expressionList;
    private ExpressionListAdapter adapter;
    private int dirId;
    private String dirName;
    private int clickPosition = -1;
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

    private View notDataView;

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

        refreshLayout.autoRefresh();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        notDataView = getLayoutInflater().inflate(R.layout.item_empty_view, (ViewGroup) recyclerView.getParent(), false);

        refreshLayout.setEnableLoadMore(false);
        toolbar.setTitle(dirName);
        gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ExpressionListAdapter(expressionList, true);
        recyclerView.setAdapter(adapter);

        expressionDialog = new ExpImageDialog.Builder(Objects.requireNonNull(this))
                .setContext(this, null, 2)
                .build();
        downloadTime.setText(createTime);

    }


    private void initData() {
        if (getIntent() != null) {
            dirId = getIntent().getIntExtra("id", 1);
            dirName = getIntent().getStringExtra("folderName");
            createTime = getIntent().getStringExtra("time");
        }
    }


    private void setAdapter() {

        new GetExpListTask(new GetExpListListener() {
            @Override
            public void onFinish(List<Expression> expressions) {
                expressionList = expressions;
                adapter.setNewData(expressions);
                adapter.notifyDataSetChanged();
                refreshLayout.finishRefresh(true);
                refreshLayout.setEnableRefresh(false);

                if (expressionList.size() == 0) {
                    adapter.setNewData(null);
                    adapter.setEmptyView(notDataView);
                }
            }
        }, true).execute(dirName);
    }

    private void initListener() {

        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                setAdapter();
            }
        });

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

        toMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行批量移动操作
                new ShowAllExpFolderTask(new TaskListener() {
                    @Override
                    public void onFinish(Object result) {
                        //执行添加的任务
                        new MoveExpTask(expressionList,checkList, (String) result,ExpLocalFolderDetailActivity.this).execute();
                    }
                },ExpLocalFolderDetailActivity.this).execute();

            }
        });
        selectDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //执行删除操作
                new DeleteImageTask(false, expressionList,checkList, dirName, ExpLocalFolderDetailActivity.this,new TaskListener() {
                    @Override
                    public void onFinish(Object result) {
                        Toasty.success(ExpLocalFolderDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < checkList.size(); i++) {
                            adapter.remove(Integer.parseInt(checkList.get(i)));
                        }
                        setContraryCheck();
                    }
                }).execute();
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
        if (Objects.equals(eventBusMessage.getType(), EventMessage.DESCRIPTION_SAVE) && clickPosition != -1) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ALog.d("更新布局" + clickPosition);
                    ALog.d(eventBusMessage.toString());
                    View view = gridLayoutManager.findViewByPosition(clickPosition).findViewById(R.id.notice);
                    view.setVisibility(View.GONE);
                    expressionList.get(clickPosition).setDesStatus(1);
                    expressionList.get(clickPosition).setDescription(eventBusMessage.getMessage());
                    EventBus.getDefault().post(new EventMessage(EventMessage.LOCAL_DESCRIPTION_SAVE, eventBusMessage.getMessage(), eventBusMessage.getMessage2(), String.valueOf(clickPosition)));

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
            //添加新的表情
            Matisse.from(ExpLocalFolderDetailActivity.this)
                    .choose(MimeType.ofAll(), false)
                    .countable(true)
                    .maxSelectable(90)
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .theme(R.style.Matisse_Dracula)
                    .imageEngine(new MyGlideEngine())
                    .forResult(1998);
        } else if (item.getItemId() == R.id.re_edit) {
            //修改表情包名称
            new MaterialDialog.Builder(this)
                    .title("输入修改后的表情包名称")
                    .content("具有一点分类意义的名字哦，方便查找")
                    .inputType(InputType.TYPE_CLASS_TEXT)
                    .input("任意文字", dirName, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(final MaterialDialog dialog, CharSequence input) {
                            List<ExpressionFolder> temExpFolderList = LitePal.where("name = ?", dialog.getInputEditText().getText().toString()).find(ExpressionFolder.class);
                            if (temExpFolderList.size() > 0) {
                                Toasty.error(ExpLocalFolderDetailActivity.this, "表情包名称已存在，请更换", Toast.LENGTH_SHORT).show();
                            } else {
                                //修改数据库的目录名称
                                new EditExpFolderNameTask(ExpLocalFolderDetailActivity.this, expressionList.size(), dirName, dialog.getInputEditText().getText().toString(), new SaveImageToGalleryListener() {
                                    @Override
                                    public void onFinish(Boolean result) {
                                        //修改本地表情包目录名称
                                        File dir = new File(GlobalConfig.appDirPath + dirName);
                                        if (dir.exists()) {
                                            dir.renameTo(new File(GlobalConfig.appDirPath + dialog.getInputEditText().getText().toString()));
                                        }
                                        toolbar.setTitle(dialog.getInputEditText().getText().toString());
                                    }
                                }).execute(expressionList);
                            }
                        }
                    }).show();
        } else if (item.getItemId() == R.id.all_download) {
            //下载到手机
            new MaterialDialog.Builder(this)
                    .title("下载提示")
                    .content("从[表情商店]下载的图片以二进制存储在本地数据库中，不[下载到本地]仍然可以离线使用，无需流量。\n\n [下载到手机]表示将图片以文件形式存在在手机存储卡中")
                    .negativeText("那就不下载了")
                    .positiveText("给我下载")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new SaveFolderToLocalTask(ExpLocalFolderDetailActivity.this, expressionList.size(), dirName).execute(expressionList);
                        }
                    })
                    .show();

        } else if (item.getItemId() == R.id.all_delete) {
            //删除本地文件
            new MaterialDialog.Builder(this)
                    .title("删除提示")
                    .content("该操作会删除表情包在手机存储卡中的文件。\n\n删除后，表情仍然以二进制保留在数据库，可以离线使用。")
                    .negativeText("取消")
                    .positiveText("给我删除")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            new DeleteExpFolderTask(dirName, ExpLocalFolderDetailActivity.this).execute();
                        }
                    })
                    .show();
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
                    if (data != null) {
                        List<String> addExpList = Matisse.obtainPathResult(data);
                        for (int i = 0; i < addExpList.size(); i++) {
                            File tempFile = new File(addExpList.get(i));
                            String fileName = tempFile.getName();
                            final Expression expression = new Expression(1, fileName, "", dirName);
                            if (!MyDataBase.addExpressionRecord(expression, tempFile)) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toasty.info(UIUtil.getContext(), expression.getName() + "文件大小太大，将不会存储").show();
                                    }
                                });
                            }
                        }
                        UIUtil.autoBackUpWhenItIsNecessary();
                        EventBus.getDefault().post(new EventMessage(EventMessage.DATABASE));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                refreshLayout.setEnableRefresh(true);
                                refreshLayout.autoRefresh();
                            }
                        });
                    }

                }
            }).start();
        }
    }
}
