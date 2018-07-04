package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ViewPagerAdapter;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.fragment.ExpressionContentFragment;
import com.ihewro.android_expression_package.util.CheckPermissionUtils;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.util.ThemeHelper;
import com.ihewro.android_expression_package.util.ToastUtil;
import com.ihewro.android_expression_package.util.UIUtil;
import com.ihewro.android_expression_package.view.CardPickerDialog;
import com.jaeger.library.StatusBarUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity implements CardPickerDialog.ClickListener, EasyPermissions.PermissionCallbacks  {

    @BindView(R.id.searchEdit)
    SearchView searchEdit;
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

    private Drawer result;
    private AccountHeader headerResult;
    private List<List<Expression>> expressionListList = new ArrayList<>();
    private List<String> pageTitleList = new ArrayList<>();
    //毫秒
    private long lastClickTime = -1;
    private long thisClickTime = -1;
    private int clickTimes = 0;

    /**
     * 由启动页面启动主活动
     * @param activity
     */
    public void actionStart(Activity activity){
        Intent intent = new Intent(activity,MainActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);


        //初始化数据
        initData();


        //初始化布局
        initView(savedInstanceState);

        //初始化权限申请
        initPermission();

    }


    private void initPermission(){
        String[] notPermission = CheckPermissionUtils.checkPermission(UIUtil.getContext());
        if (notPermission.length!=0){//需要的权限没有全部被运行
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
        Toast.makeText(UIUtil.getContext(),"权限申请成功，愉快使用表情宝宝吧😁",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> list) {
        // 权限被拒绝
        Toast.makeText(UIUtil.getContext(),"权限没有被通过，该软件运行过程中可能会闪退，请留意",Toast.LENGTH_SHORT).show();
    }


    /**
     * 初始化布局
     * @param savedInstanceState
     */
    private void initView(Bundle savedInstanceState){



        //初始化侧边栏
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withCompactStyle(false)
                .withHeaderBackground(R.drawable.bg)
                .withSavedInstance(savedInstanceState)
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        if (lastClickTime == -1){
                            lastClickTime = System.currentTimeMillis();
                            thisClickTime = System.currentTimeMillis();
                            ToastUtil.showMessageShort("你戳我？很痛哎");
                        }else {//不是第一次点击的
                            thisClickTime = System.currentTimeMillis();
                            if (thisClickTime - lastClickTime < 500){//是在0.8秒内点击的
                                lastClickTime = thisClickTime;
                                clickTimes ++;
                                switch (clickTimes){
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
                                        ToastUtil.showMessageShort("去找你我聊天吧，用我的表情包，哈哈哈哈哈");
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
                            }else{//已经超过连续点击的时间，将变量初始化
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


        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withFullscreen(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("主页").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1).withSelectedTextColor(getResources().getColor(R.color.theme_color_primary)).withSelectedIconColor(getResources().getColor(R.color.theme_color_primary)).withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                            @Override
                            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                result.closeDrawer();
                                return false;
                            }
                        }),
                        new PrimaryDrawerItem().withName("表情商店").withIcon(FontAwesome.Icon.faw_gamepad).withSelectedTextColor(getResources().getColor(R.color.theme_color_primary)).withSelectedIconColor(getResources().getColor(R.color.theme_color_primary)),
                        new PrimaryDrawerItem().withName("我的").withIcon(FontAwesome.Icon.faw_user).withSelectedTextColor(getResources().getColor(R.color.theme_color_primary)).withSelectedIconColor(getResources().getColor(R.color.theme_color_primary)).withEnabled(false),
                        new PrimaryDrawerItem().withName("换肤").withSelectedTextColor(getResources().getColor(R.color.theme_color_primary)).withSelectedIconColor(getResources().getColor(R.color.theme_color_primary)).withIcon(GoogleMaterial.Icon.gmd_color_lens),
                        new SectionDrawerItem().withName("其他"),
                        new SecondaryDrawerItem().withName("设置").withSelectedTextColor(getResources().getColor(R.color.theme_color_primary)).withSelectedIconColor(getResources().getColor(R.color.theme_color_primary)).withIcon(FontAwesome.Icon.faw_cog).withEnabled(false),
                        new SecondaryDrawerItem().withName("五星好评").withSelectedTextColor(getResources().getColor(R.color.theme_color_primary)).withSelectedIconColor(getResources().getColor(R.color.theme_color_primary)).withIcon(FontAwesome.Icon.faw_question).withEnabled(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 2://进入表情商店
                                ShopActivity.actionStart(MainActivity.this);
                                break;
                            case 4://切换主题
                                CardPickerDialog dialog = new CardPickerDialog();
                                dialog.setClickListener(MainActivity.this);
                                dialog.show(getSupportFragmentManager(), CardPickerDialog.TAG);
                                break;
                        }

                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();


        //初始化TabLayout
        initTabLayout();

        //设置沉浸式状态栏
        StatusBarUtil.setTranslucentForImageViewInFragment(this, toolbar);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mainItem.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, -(UIUtil.getStatusBarHeight(this)),
                layoutParams.rightMargin, layoutParams.bottomMargin);
    }


    /**
     * 初始化表情包数据
     * 这个表情包是内置在apk中，用户无需下载即可直接使用
     */
    private void initData(){
        AssetManager assetManager = getAssets();
        String[] files =null;
        try{
            files = assetManager.list("imagehuyi");
        }catch(IOException e){
            Log.e("tag", e.getMessage());
        }

        for (int i =0;i<files.length;i++){
            Log.e("filelist",files[i]);
            String []tempFiles = null;
            List<Expression> expressionList = new ArrayList<>();
            pageTitleList.add(files[i]);
            try {
                tempFiles = assetManager.list(GlobalConfig.assetsFolderName + "/" + files[i]);
                for (String tempFile : tempFiles) {
                    Log.d("filename",tempFile);
                    expressionList.add(new Expression(-1, tempFile, GlobalConfig.assetsFolderName +  "/" + files[i] + "/" + tempFile,files[i]));
                }
                expressionListList.add(expressionList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 初始化TabLayout 数据
     */
    private void initTabLayout() {
        setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

    }

    /**
     * 设置ViewPager
     */
    private void setViewPager(ViewPager viewPager) {
        //碎片列表
        List<Fragment> fragmentList = new ArrayList<>();

        for (int i =0;i<expressionListList.size();i++) {

            ExpressionContentFragment fragment = new ExpressionContentFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) expressionListList.get(i));
            bundle.putString("name",pageTitleList.get(i));
            fragment.setArguments(bundle);
            fragmentList.add(fragment);
        }




        //标题列表
        //pageTitleList.clear();
        //pageTitleList.add("坏坏");
        //pageTitleList.add("猥琐萌");


        //新建适配器
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), fragmentList, pageTitleList);

        //设置ViewPager
        viewPager.setAdapter(adapter);

    }


    @Override
    public void onConfirm(int currentTheme) {
        if (ThemeHelper.getTheme(MainActivity.this) != currentTheme) {
            ThemeHelper.setTheme(MainActivity.this, currentTheme);
            ThemeUtils.refreshUI(MainActivity.this, new ThemeUtils.ExtraRefreshable() {
                        @Override
                        public void refreshGlobal(Activity activity) {
                            //for global setting, just do once
                            if (Build.VERSION.SDK_INT >= 21) {
                                final MainActivity context = MainActivity.this;
                                ActivityManager.TaskDescription taskDescription =
                                        new ActivityManager.TaskDescription(null, null,
                                                ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary));
                                setTaskDescription(taskDescription);
                            }
                        }

                        @Override
                        public void refreshSpecificView(View view) {
                            //TODO: will do this for each traversal

                            tabLayout.setBackgroundColor(ThemeUtils.getThemeAttrColor(MainActivity.this, android.R.attr.colorPrimary));
                            /*List<IDrawerItem> iDrawerItems = result.getOriginalDrawerItems();
                            for (int i =0; i < iDrawerItems.size(); i++){
                                PrimaryDrawerItem item = (PrimaryDrawerItem)iDrawerItems.get(i);
                                item.withSelectedColor(android.R.attr.colorPrimary);
                            }*/
                            result.closeDrawer();
                        }


                    }
            );
        }
    }
}
