package com.ihewro.android_expression_package.fragment;


import android.content.Entity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.ALog;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.adapter.ExpressionListAdapter;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.view.ExpImageDialog;

import java.io.IOException;
import java.io.Serializable;
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
    private List<Expression> expressionList;
    private ExpImageDialog expressionDialog;
    private int currentPosition = -1;


    public static Fragment fragmentInstant(String data,String name){
        ExpressionContentFragment fragment = new ExpressionContentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", data);//json字符串
        bundle.putString("name",name);
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

        //初始化弹出层相关信息
        initView();


        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        GridLayoutManager gridLayoutManager =  new GridLayoutManager(getActivity(),4);
        recyclerView.setLayoutManager(gridLayoutManager);

        initExpressionData();

        adapter = new ExpressionListAdapter(R.layout.item_expression,expressionList);
        recyclerView.setAdapter(adapter);

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


    /**
     * 初始化表情包数据
     */
    private void initExpressionData(){
        Bundle bundle = getArguments();
        assert bundle != null;
        try {
            String jsonString = bundle.getString("data");
            ObjectMapper mapper = new ObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, Expression.class);
            expressionList = mapper.readValue(jsonString, javaType);
            ALog.d("list",expressionList.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void initView(){
        expressionDialog  = new ExpImageDialog.Builder(Objects.requireNonNull(getActivity()))
                .setContext(getActivity(),this)
                .build();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
