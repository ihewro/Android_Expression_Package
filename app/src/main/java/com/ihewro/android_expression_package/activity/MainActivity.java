package com.ihewro.android_expression_package.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.canking.minipay.Config;
import com.canking.minipay.MiniPayUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihewro.android_expression_package.MyDataBase;
import com.ihewro.android_expression_package.MySharePreference;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ViewPagerAdapter;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.OneDetail;
import com.ihewro.android_expression_package.bean.OneDetailList;
import com.ihewro.android_expression_package.callback.RemoveCacheListener;
import com.ihewro.android_expression_package.fragment.ExpressionContentFragment;
import com.ihewro.android_expression_package.http.HttpUtil;
import com.ihewro.android_expression_package.task.CheckUpdateTask;
import com.ihewro.android_expression_package.task.RemoveCacheTask;
import com.ihewro.android_expression_package.util.APKVersionCodeUtils;
import com.ihewro.android_expression_package.util.CheckPermissionUtils;
import com.ihewro.android_expression_package.util.DataCleanManager;
import com.ihewro.android_expression_package.util.ToastUtil;
import com.ihewro.android_expression_package.util.UIUtil;
import com.ihewro.android_expression_package.view.CustomImageView;
import com.ihewro.android_expression_package.view.ExpImageDialog;
import com.ihewro.android_expression_package.view.GuideView;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import me.jessyan.progressmanager.ProgressListener;
import me.jessyan.progressmanager.ProgressManager;
import me.jessyan.progressmanager.body.ProgressInfo;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.bumptech.glide.request.RequestOptions.bitmapTransform;
import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @BindView(R.id.main_item)
    CoordinatorLayout mainItem;
    @BindView(R.id.top_image)
    CustomImageView topImage;
    @BindView(R.id.one_text)
    TextView oneText;
    @BindView(R.id.add_exp)
    ImageView addExp;
    private GuideView guideRefreshView;
    private GuideView guideAddView;


    private Drawer result;
    private AccountHeader headerResult;
    private List<ExpressionFolder> expressionFolderList = new ArrayList<>();
    //毫秒
    private long lastClickTime = -1;
    private long thisClickTime = -1;
    private int clickTimes = 0;

    private MenuItem refreshItem;
    private ImageView refreshView;

    private int oneItem = 0;//one的序号

    private ViewPagerAdapter adapter;

    private SecondaryDrawerItem removeCache;

    private boolean isFirst;//是否是首次打开app

    /**
     * 由启动页面启动主活动
     *
     * @param activity
     */
    public static void actionStart(Activity activity, String jsonString) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("data", jsonString);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        EventBus.getDefault().register(this);

        //初始化数据
        initData();


        //初始化布局
        initView(savedInstanceState);

        //初始化权限申请
        initPermission();

        //监听器
        initListener();

        if (!isFirst){
            getOne(refreshItem);
        }

        //获取缓存大小
        setCacheSize();



    }

    private void initGuideView(){
        View customView = LayoutInflater.from(this).inflate(R.layout.guide_view, null);
        guideRefreshView = GuideView.Builder
                .newInstance(this)
                .setTargetView(refreshItem.getActionView())//设置目标
                .setCustomGuideView(customView)
                .setDirction(GuideView.Direction.LEFT_BOTTOM)
                .setShape(GuideView.MyShape.CIRCULAR)   // 设置圆形显示区域，
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        getOne(refreshItem);
                        guideRefreshView.hide();
                        initGuideAddView();
                    }
                })
                .build();

        guideRefreshView.show();
    }


    private void initGuideAddView(){

        result.getRecyclerView().post(new Runnable()
        {
            @Override
            public void run()
            {
                View customView = LayoutInflater.from(MainActivity.this).inflate(R.layout.guide_view, null);
                ((TextView)customView.findViewById(R.id.textView5)).setText("点击可以下载网络上热门表情包，不断更新！");
                guideAddView = GuideView.Builder
                        .newInstance(MainActivity.this)
                        .setTargetView(addExp)//设置目标
                        .setCustomGuideView(customView)
                        .setDirction(GuideView.Direction.LEFT_BOTTOM)
                        .setShape(GuideView.MyShape.CIRCULAR)   // 设置圆形显示区域，
                        .setBgColor(getResources().getColor(R.color.shadow))
                        .setOnclickListener(new GuideView.OnClickCallback() {
                            @Override
                            public void onClickedGuideView() {
                                guideAddView.hide();
                                Toasty.info(MainActivity.this,"侧边栏还有一些更多有趣的功能入口，程序还有一些彩蛋等你发现",Toast.LENGTH_SHORT).show();
                                result.openDrawer();
                            }
                        })
                        .build();

                guideAddView.show();
            }
        });

        MySharePreference.setIsFistEnter(this);

    }


    private void initListener() {
        addExp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShopActivity.actionStart(MainActivity.this);
            }
        });

        oneText.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                android.content.ClipboardManager clipboardManager = (android.content.ClipboardManager) MainActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, oneText.getText()));
                Toasty.success(MainActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }


    private void initPermission() {
        String[] notPermission = CheckPermissionUtils.checkPermission(UIUtil.getContext());
        if (notPermission.length != 0) {//需要的权限没有全部被运行
            ActivityCompat.requestPermissions(this, notPermission, 100);
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> list) {
        //权限被申请成功
        Toast.makeText(UIUtil.getContext(), "权限申请成功，愉快使用表情宝宝吧😁", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // 权限被拒绝
        Toast.makeText(UIUtil.getContext(), "权限没有被通过，该软件运行过程中可能会闪退，请留意", Toast.LENGTH_SHORT).show();
    }


    /**
     * 初始化布局
     *
     * @param savedInstanceState
     */
    private void initView(Bundle savedInstanceState) {

        //初始化侧边栏
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withHeaderBackground(R.drawable.header)
                .withSavedInstance(savedInstanceState)
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        if (lastClickTime == -1) {
                            lastClickTime = System.currentTimeMillis();
                            thisClickTime = System.currentTimeMillis();
                            ToastUtil.showMessageShort("你戳我？很痛哎");
                        } else {//不是第一次点击的
                            thisClickTime = System.currentTimeMillis();
                            if (thisClickTime - lastClickTime < 500) {//是在0.8秒内点击的
                                lastClickTime = thisClickTime;
                                clickTimes++;
                                switch (clickTimes) {
                                    case 3:
                                        ToastUtil.showMessageShort("还戳！！！");
                                        break;

                                    case 10:
                                        ToastUtil.showMessageShort("好玩吗");
                                        break;

                                    case 20:
                                        ToastUtil.showMessageShort("很无聊？");
                                        break;

                                    case 40:
                                        ToastUtil.showMessageShort("。。。");
                                        break;

                                    case 50:
                                        ToastUtil.showMessageShort("其实我是一个炸弹💣");
                                        break;

                                    case 60:
                                        ToastUtil.showMessageShort("是不是吓坏了哈哈，骗你的");
                                        break;

                                    case 70:
                                        ToastUtil.showMessageShort("看你还能坚持多久");
                                        break;

                                    case 90:
                                        ToastUtil.showMessageShort("哇！！！就问你手指痛吗");
                                        break;

                                    case 110:
                                        ToastUtil.showMessageShort("其实，生活还有很多有意义的事情做，比如。。。。");
                                        break;

                                    case 120:
                                        ToastUtil.showMessageShort("比如找我聊天啊，别戳了喂");
                                        break;

                                    case 130:
                                        ToastUtil.showMessageShort("去找我聊天吧，用我的表情包，哈哈哈哈哈");
                                        break;

                                    case 140:
                                        ToastUtil.showMessageShort("我走了，祝你玩得开心");
                                        break;

                                    case 150:
                                        ToastUtil.showMessageShort("哈哈哈，其实我没走哦，看你这么努力，告诉你一个秘密");
                                        break;

                                    case 160:
                                        ToastUtil.showMessageShort("我喜欢你( *︾▽︾)，这次真的要再见了哦👋，再见");
                                        result.closeDrawer();//关闭侧边栏
                                        break;

                                }
                            } else {//已经超过连续点击的时间，将变量初始化
                                lastClickTime = -1;
                                thisClickTime = -1;
                                clickTimes = 0;
                            }

                        }
                        //ToastUtil.showMessageShort("点击了");
                        return false;
                    }
                })
                .build();
        removeCache = new SecondaryDrawerItem().withName("清除缓存").withIcon(GoogleMaterial.Icon.gmd_delete).withSelectable(false);

        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withFullscreen(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        new SecondaryDrawerItem().withName("我的主页").withIcon(GoogleMaterial.Icon.gmd_home).withSelectable(false),//1
                        new SecondaryDrawerItem().withName("表情商店").withIcon(GoogleMaterial.Icon.gmd_add_shopping_cart).withSelectable(false),//2
                        new SecondaryDrawerItem().withName("我的表情").withIcon(GoogleMaterial.Icon.gmd_photo_library).withSelectable(false),//3
                        removeCache,//4
                        new SecondaryDrawerItem().withName("退出应用").withIcon(GoogleMaterial.Icon.gmd_exit_to_app).withSelectable(false),//5
                        new DividerDrawerItem(),//6
                        new SecondaryDrawerItem().withName("关于应用").withIcon(R.drawable.logo).withSelectable(false),//7
                        new SecondaryDrawerItem().withName("五星好评").withIcon(GoogleMaterial.Icon.gmd_favorite).withSelectable(false),//8
                        new SecondaryDrawerItem().withName("捐赠我们").withIcon(GoogleMaterial.Icon.gmd_payment).withSelectable(false),//9
                        new SecondaryDrawerItem().withName("检查更新").withIcon(GoogleMaterial.Icon.gmd_system_update_alt).withSelectable(false).withDescription("v" + APKVersionCodeUtils.getVerName(MainActivity.this) + "(" + APKVersionCodeUtils.getVersionCode(MainActivity.this) + ")")//10
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1://我的首页，没卵用的一个按钮
                                result.closeDrawer();
                                break;
                            case 2://进入表情商店
                                ShopActivity.actionStart(MainActivity.this);
                                break;
                            case 3: //进入我的表情管理
                                MyActivity.actionStart(MainActivity.this);
                                break;
                            case 4://清除缓存
                                MaterialDialog dialog;
                                new MaterialDialog.Builder(MainActivity.this)
                                        .title("操作通知")
                                        .content("浏览网络信息或带来一些本地缓存，你可以选择清理他们，但再次访问需要重新下载，确定清理吗？")
                                        .positiveText("确定")
                                        .negativeText("先留着吧，手机空间有的是")
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                new RemoveCacheTask(MainActivity.this, new RemoveCacheListener() {
                                                    @Override
                                                    public void onFinish() {
                                                        setCacheSize();
                                                    }
                                                }).execute();
                                            }
                                        })
                                        .show();
                                break;
                            case 5://退出应用
                                finish();
                                break;
                            case 7://关于我们
                                AboutActivity.actionStart(MainActivity.this);
                                break;
                            case 8://五星好评
                                Uri uri = Uri.parse("market://details?id=" + UIUtil.getContext().getPackageName());
                                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                                try {
                                    startActivity(goToMarket);
                                } catch (ActivityNotFoundException e) {
                                    Toasty.error(MainActivity.this, "无法启动应用市场，请重试", Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 9://捐赠
                                MiniPayUtils.setupPay(MainActivity.this, new Config.Builder("FKX07840DBMQMUHP92W1DD", R.drawable.alipay, R.drawable.wechatpay).build());
                                break;

                            case 10://检查更新
                                new CheckUpdateTask(MainActivity.this).execute();
                                break;
                        }

                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View drawerView) {
                        setCacheSize();
                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {

                    }

                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {

                    }
                })
                .build();



        //初始化TabLayout
        initTabLayout(false);

        //设置沉浸式状态栏
        //StatusBarUtil.setTranslucentForImageViewInFragment(this, toolbar);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mainItem.getLayoutParams();
        /*layoutParams.setMargins(layoutParams.leftMargin, -(UIUtil.getStatusBarHeight(this)),
                layoutParams.rightMargin, layoutParams.bottomMargin);*/


    }


    /**
     * 初始化表情包数据
     * 这个表情包是内置在apk中，用户无需下载即可直接使用
     */
    private void initData() {

        //TODO: 读取sharePreference查看是否是首次进入app
        isFirst = MySharePreference.getIsFirstEnter(this);
        if (getIntent() != null) {
            try {
                String jsonString = getIntent().getStringExtra("data");
                ObjectMapper mapper = new ObjectMapper();
                JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, ExpressionFolder.class);
                expressionFolderList = mapper.readValue(jsonString, javaType);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateData() {
        expressionFolderList = LitePal.findAll(ExpressionFolder.class, true);
    }


    /**
     * 初始化TabLayout 数据
     */
    private void initTabLayout(boolean isUpdate) {
        setViewPager(viewPager, isUpdate);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

    }


    /**
     * 设置ViewPager
     */
    private void setViewPager(ViewPager viewPager, boolean isUpdate) {
        if (isUpdate) {
            viewPager.removeAllViewsInLayout();
        }
        ALog.d("表情包的数目" + expressionFolderList.size());
        //碎片列表
        List<String> pageTitleList = new ArrayList<>();
        List<Fragment> fragmentList = new ArrayList<>();
        if (expressionFolderList.size() == 0) {//如果没有表情包目录，则会显示为空
            fragmentList.add(ExpressionContentFragment.fragmentInstant("", "默认"));
            pageTitleList.add("默认");
        } else {
            for (int i = 0; i < expressionFolderList.size(); i++) {
                if (expressionFolderList.get(i).getExpressionList().size() == 0 || expressionFolderList.get(i).getExpressionList() == null) {
                    //过滤掉空文件夹
                    ALog.d("该表情包的表情数目为0");
                } else {
                    ALog.d("该表情包的表情数目为 " + expressionFolderList.get(i).getExpressionList().size());
                    try {
                        ObjectMapper mapper = new ObjectMapper();
                        String jsonString = mapper.writeValueAsString(expressionFolderList.get(i).getExpressionList());
                        fragmentList.add(ExpressionContentFragment.fragmentInstant(jsonString, expressionFolderList.get(i).getName()));
                        pageTitleList.add(expressionFolderList.get(i).getName());
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        //新建适配器
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, pageTitleList);
        //设置ViewPager
        viewPager.setAdapter(adapter);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        refreshItem = menu.findItem(R.id.refresh);
        showRefreshAnimation(refreshItem);
        if(isFirst){
            initGuideView();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            getOne(item);
        } else if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void showRefreshAnimation(MenuItem item) {

        hideRefreshAnimation();
        refreshItem = item;

        //这里使用一个ImageView设置成MenuItem的ActionView，这样我们就可以使用这个ImageView显示旋转动画了
        View refreshActionView = getLayoutInflater().inflate(R.layout.item_refresh_menu, null);

        item.setActionView(refreshActionView);

        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        refreshActionView.setAnimation(rotateAnimation);
        refreshActionView.startAnimation(rotateAnimation);
    }

    @SuppressLint("NewApi")
    private void hideRefreshAnimation() {
        if (refreshItem != null) {
            View view = refreshItem.getActionView();
            if (view != null) {
                view.clearAnimation();
                refreshItem.setActionView(null);
            }
        }
    }


    private void getOne(MenuItem item) {
        if (item != null) {
            //刷新头图信息
            showRefreshAnimation(item);
        }

        if (MyDataBase.isNeedGetOnes()) {//如果已经过时了，则需要从网络上请求数据
            HttpUtil.getOnes(new Callback<OneDetailList>() {
                @Override
                public void onResponse(@NonNull Call<OneDetailList> call, @NonNull Response<OneDetailList> response) {

                    //获取数据成功后删除旧的数据
                    LitePal.deleteAll(OneDetailList.class);
                    LitePal.deleteAll(OneDetail.class);

                    //存储新的数据
                    final OneDetailList oneDetailList = response.body();
                    assert oneDetailList != null;
                    oneDetailList.save();

                    for (int i = 0; i < oneDetailList.getCount(); i++) {
                        OneDetail oneDetail = oneDetailList.getOneDetailList().get(i);
                        oneDetail.setOneDetailList(oneDetailList);
                        oneDetail.save();
                    }

                    setOneUI(oneDetailList);
                }

                @Override
                public void onFailure(@NonNull Call<OneDetailList> call, @NonNull Throwable t) {
                    //什么也不做
                    Toasty.error(MainActivity.this, "请求一个失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    ALog.d("请求失败" + t.getMessage());
                }
            });
        } else {
            setOneUI(LitePal.findFirst(OneDetailList.class, true));
        }

    }

    private void setOneUI(final OneDetailList oneDetailLists) {
        final List<OneDetail> oneDetailList = oneDetailLists.getOneDetailList();
        final int currentItem = oneItem % oneDetailList.size();
        OneDetail oneDetail = oneDetailList.get(currentItem);
        oneText.setText(oneDetail.getText());
        ProgressManager.getInstance().addResponseListener(oneDetail.getImgUrl(), getGlideListener());//监听glide进度，加载完毕后，取消动画

        Glide.with(this).load(oneDetail.getImgUrl())
                .apply(bitmapTransform(new MultiTransformation(new CenterCrop(), new RoundedCornersTransformation(35, 0))))
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        topImage.setImageDrawable(resource);
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    sleep(1500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        hideRefreshAnimation();
                                    }
                                });
                            }
                        }).start();
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Toasty.error(MainActivity.this, "请求图片失败，请稍后重试", Toast.LENGTH_SHORT).show();
                    }
                });
        oneItem++;//这样下次刷新显示下一条

        topImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Expression expression = new Expression(2, oneDetailLists.getDate().substring(0, 10) + (currentItem) + ".jpg", oneDetailList.get(currentItem).getImgUrl(), "头图");
                ExpImageDialog expImageDialog = new ExpImageDialog.Builder(MainActivity.this)
                        .setContext(MainActivity.this, null)
                        .build();
                expImageDialog.setImageData(expression);
                expImageDialog.show();
            }
        });
    }


    @NonNull
    private ProgressListener getGlideListener() {
        return new ProgressListener() {
            @Override
            public void onProgress(ProgressInfo progressInfo) {
                int progress = progressInfo.getPercent();
                ALog.d("progress233", progress);
            }

            @Override
            public void onError(long id, Exception e) {

            }
        };
    }


    long startTime = 0;

    @Override
    public void onBackPressed() {

        if (result.isDrawerOpen()){
            result.closeDrawer();
        }else {
            long currentTime = System.currentTimeMillis();
            if ((currentTime - startTime) >= 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
                startTime = currentTime;
            } else {
                finish();
            }
        }
    }


    private void setCacheSize() {
        //获得应用内部缓存(/data/data/com.example.androidclearcache/cache)
        final File file = new File(getCacheDir().getPath());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final String cacheSize = DataCleanManager.getCacheSize(file);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ALog.d("cahceSize",cacheSize);
                            removeCache.withDescription(cacheSize);
                            result.updateItem(removeCache);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void refreshUI(EventMessage eventBusMessage){
        if (Objects.equals(eventBusMessage.getType(), EventMessage.DATABASE)){
            ALog.d("更新首页布局");
            updateData();
            initTabLayout(true);
        }
    }


}
