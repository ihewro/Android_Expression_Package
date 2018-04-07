package com.ihewro.android_expression_package.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.util.UIUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import pl.droidsonroids.gif.GifImageView;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/04/06
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ExpressionListAdapter extends BaseQuickAdapter<Expression, ExpressionListAdapter.IViewHolder> {

    private List<Expression> expressionList;

    public ExpressionListAdapter(int layoutResId, @Nullable List<Expression> data) {
        super(layoutResId, data);
        expressionList = data;
    }

    @Override
    protected void convert(ExpressionListAdapter.IViewHolder helper, Expression item) {
        UIUtil.setImageToImageView(item.getStatus(),item.getUrl(), (GifImageView) helper.getView(R.id.iv_expression));
    }


    public static class IViewHolder extends BaseViewHolder{

        GifImageView IvExpression;

        public IViewHolder(View itemView) {
            super(itemView);
            IvExpression = itemView.findViewById(R.id.iv_expression);
        }
    }

}
