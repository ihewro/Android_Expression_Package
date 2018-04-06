package com.ihewro.android_expression_package.activity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bilibili.magicasakura.utils.ThemeUtils;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ViewPagerAdapter;
import com.ihewro.android_expression_package.fragment.ExpressionContentFragment;
import com.ihewro.android_expression_package.util.ThemeHelper;
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
import com.mikepenz.materialize.color.Material;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

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


        initTabLayout();

        StatusBarUtil.setTranslucentForImageViewInFragment(this, toolbar);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) mainItem.getLayoutParams();
        layoutParams.setMargins(layoutParams.leftMargin, -(getStatusBarHeight(this)),
                layoutParams.rightMargin, layoutParams.bottomMargin);

    }


    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
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
        ExpressionContentFragment currentAudioIntroFragment = new ExpressionContentFragment();
        Bundle bundle = new Bundle();


        currentAudioIntroFragment.setArguments(bundle);
        fragmentList.add(currentAudioIntroFragment);
        fragmentList.add(new ExpressionContentFragment());


        //标题列表
        List<String> pageTitleList = new ArrayList<>();
        pageTitleList.add("坏坏");
        pageTitleList.add("猥琐萌");


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
