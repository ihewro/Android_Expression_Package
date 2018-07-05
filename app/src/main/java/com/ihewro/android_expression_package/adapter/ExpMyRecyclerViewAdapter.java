package com.ihewro.android_expression_package.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.bean.local.LocalExpressionFolder;
import com.ihewro.android_expression_package.bean.web.WebExpressionFolder;

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
public class ExpMyRecyclerViewAdapter extends BaseQuickAdapter<LocalExpressionFolder, BaseViewHolder> {

    private Activity activity;
    public ExpMyRecyclerViewAdapter(@Nullable List<LocalExpressionFolder> data, Activity activity) {
        super(R.layout.item_exp_my,data);
        this.activity = activity;
    }

    @Override
    protected void convert(BaseViewHolder helper, LocalExpressionFolder item) {



    }
}
