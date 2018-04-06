package com.ihewro.android_expression_package.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.bean.Expression;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/04/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ExpressionListAdapter extends BaseQuickAdapter<Expression, BaseViewHolder> {

    List<Expression> expressionList;

    public ExpressionListAdapter(@Nullable List<Expression> data) {
        super(R.layout.item_expression,data);
        expressionList = data;
    }

    @Override
    protected void convert(BaseViewHolder helper, Expression item) {
        helper.setImageResource(R.id.iv_expression,R.drawable.bg);
    }
}
