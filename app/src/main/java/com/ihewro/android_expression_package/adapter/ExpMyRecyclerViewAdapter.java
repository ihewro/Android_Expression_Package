package com.ihewro.android_expression_package.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.bean.ExpressionFolder;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/05
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ExpMyRecyclerViewAdapter extends BaseQuickAdapter<ExpressionFolder, BaseViewHolder> {

    private Activity activity;
    public ExpMyRecyclerViewAdapter(@Nullable List<ExpressionFolder> data, Activity activity) {
        super(R.layout.item_exp_my,data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, ExpressionFolder item) {



    }
}
