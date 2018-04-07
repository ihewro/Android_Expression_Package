package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ViewPagerAdapter;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.fragment.ExpressionContentFragment;
import com.ihewro.android_expression_package.util.ThemeHelper;
import com.ihewro.android_expression_package.util.UIUtil;
import com.ihewro.android_expression_package.view.CardPickerDialog;
import com.jaeger.library.StatusBarUtil;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.context.IconicsLayoutInflater2;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialize.color.Material;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements CardPickerDialog.ClickListener  {

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
                .build();

        result = new DrawerBuilder()
                .withActivity(this)
                .withAccountHeader(headerResult)
                .withToolbar(toolbar)
                .withFullscreen(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("主页").withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName("表情商店").withIcon(FontAwesome.Icon.faw_gamepad),
                        new PrimaryDrawerItem().withName("我的").withIcon(FontAwesome.Icon.faw_eye),
                        new PrimaryDrawerItem().withName("换肤").withIcon(GoogleMaterial.Icon.gmd_color_lens),
                        new SectionDrawerItem().withName("其他"),
                        new SecondaryDrawerItem().withName("设置").withIcon(FontAwesome.Icon.faw_cog),
                        new SecondaryDrawerItem().withName("五星好评").withIcon(FontAwesome.Icon.faw_question).withEnabled(false)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position){
                            case 4:
                                CardPickerDialog dialog = new CardPickerDialog();
                                dialog.setClickListener(MainActivity.this);
                                dialog.show(getSupportFragmentManager(), CardPickerDialog.TAG);
                                break;
                        }

                        Toast.makeText(getApplicationContext(),position + "位置",Toast.LENGTH_SHORT).show();
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
                tempFiles = assetManager.list("imagehuyi/" + files[i]);
                for (String tempFile : tempFiles) {
                    Log.d("filename",tempFile);
                    expressionList.add(new Expression(-1, tempFile, "imagehuyi/" + files[i] + "/" + tempFile));
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

        for (List<Expression> expressionList:expressionListList) {

            ExpressionContentFragment fragment = new ExpressionContentFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) expressionList);
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
                        }
                    }
            );
        }
    }
}
