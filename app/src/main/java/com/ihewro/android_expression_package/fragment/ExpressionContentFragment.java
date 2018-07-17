package com.ihewro.android_expression_package.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.EventMessage;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.GetExpListListener;
import com.ihewro.android_expression_package.task.GetExpListTask;
import com.ihewro.android_expression_package.util.UIUtil;
import com.ihewro.android_expression_package.view.ExpImageDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class ExpressionContentFragment extends Fragment {


    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    Unbinder unbinder;

    private ExpressionListAdapter adapter;
    private List<Expression> expressionList = new ArrayList<>();
    private ExpImageDialog expressionDialog;
    private int currentPosition = -1;
    private View notDataView;
    private View loadingView;
    private String tabName;
    private boolean isNotShow;
    private GridLayoutManager gridLayoutManager;
    private GetExpListTask task;
    private boolean isLoadData;//是否加载数据了

    int currentTabPos;
    int tabPos;

    public static Fragment fragmentInstant(String name,boolean isNotShow,int tabPos){
        ExpressionContentFragment fragment = new ExpressionContentFragment();
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putBoolean("isNotShow",isNotShow);
        bundle.putInt("tabpos",tabPos);
        fragment.setArguments(bundle);
        return fragment;
    }



    public ExpressionContentFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expression_content, container, false);
        unbinder = ButterKnife.bind(this, view);
        notDataView = getLayoutInflater().inflate(R.layout.item_empty_view, (ViewGroup) recyclerView.getParent(), false);
        loadingView = getLayoutInflater().inflate(R.layout.loading_view, (ViewGroup) recyclerView.getParent(), false);

        if (!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        initData();

        //初始化界面空布局
        initView();

        initListener();

        setExpressionData();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ALog.d("重新创建了该fragment");


    }


    private void initListener(){
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                currentPosition = position;
                Expression expression = expressionList.get(position);
                expressionDialog.setImageData(expression);
                expressionDialog.show();

            }
        });
    }



    private void initData(){
        Bundle bundle = getArguments();
        assert bundle != null;
        tabName = bundle.getString("name");
        isNotShow = bundle.getBoolean("isNotShow");
        tabPos = bundle.getInt("tabpos",0);
    }

    /**
     * 初始化表情包数据
     */
    private void setExpressionData(){

        task = new GetExpListTask(new GetExpListListener() {
            @Override
            public void onFinish(List<Expression> expressions) {
                expressionList = expressions;
                adapter.setNewData(expressions);
                if (expressions.size() == 0){
                    adapter.setNewData(null);
                    adapter.setEmptyView(notDataView);
                }
                isLoadData = true;
            }
        },true);
        task.execute(tabName);
    }

    private void initView(){
        expressionDialog  = new ExpImageDialog.Builder(Objects.requireNonNull(getActivity()))
                .setContext(getActivity(),this,3)
                .build();


        gridLayoutManager =  new GridLayoutManager(getActivity(),4);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new ExpressionListAdapter(expressionList,true);
        recyclerView.setAdapter(adapter);
        adapter.setNewData(null);
        ImageView imageView = loadingView.findViewById(R.id.imageView5);
        Animation rotateAnimation = AnimationUtils.loadAnimation(UIUtil.getContext(), R.anim.rotate);
        imageView.setAnimation(rotateAnimation);
        imageView.startAnimation(rotateAnimation);
        adapter.setEmptyView(loadingView);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().unregister(this);
        }
        unbinder.unbind();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshUI(EventMessage eventBusMessage) {
        ALog.d(eventBusMessage);
        if (Objects.equals(eventBusMessage.getType(), EventMessage.LOCAL_DESCRIPTION_SAVE)) {
            if (Objects.equals(eventBusMessage.getMessage2(), tabName)) {
                ALog.d("当前位置" + currentPosition);
                if(eventBusMessage.getMessage3()!=null || eventBusMessage.getMessage3()!=""){
                    currentPosition = Integer.parseInt(eventBusMessage.getMessage3());
                }
                View view = gridLayoutManager.findViewByPosition(currentPosition).findViewById(R.id.notice);
                view.setVisibility(View.GONE);

                expressionList.get(currentPosition).setDesStatus(1);
                expressionList.get(currentPosition).setDescription(eventBusMessage.getMessage());
            }
        }else if (Objects.equals(eventBusMessage.getType(), EventMessage.DESCRIPTION_SAVE)){
            if (Integer.parseInt(eventBusMessage.getMessage3()) == 3 && currentPosition != -1){
                ALog.d("当前位置" + currentPosition);

                View view = gridLayoutManager.findViewByPosition(currentPosition).findViewById(R.id.notice);
                view.setVisibility(View.GONE);
                expressionList.get(currentPosition).setDesStatus(1);
                expressionList.get(currentPosition).setDescription(eventBusMessage.getMessage());
            }
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
}
