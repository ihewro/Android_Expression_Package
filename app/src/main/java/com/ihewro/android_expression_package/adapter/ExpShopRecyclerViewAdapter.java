package com.ihewro.android_expression_package.adapter;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.ALog;
import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.activity.ExpFolderDetailActivity;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.web.WebExpressionFolder;
import com.ihewro.android_expression_package.util.UIUtil;

import java.util.List;

import butterknife.BindView;
import pl.droidsonroids.gif.GifImageView;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class ExpShopRecyclerViewAdapter extends BaseQuickAdapter<WebExpressionFolder, BaseViewHolder> {
    @BindView(R.id.exp_name)
    TextView expName;
    @BindView(R.id.image_1)
    ImageView image1;
    @BindView(R.id.image_2)
    ImageView image2;
    @BindView(R.id.image_3)
    ImageView image3;
    @BindView(R.id.image_4)
    ImageView image4;
    @BindView(R.id.image_5)
    ImageView image5;
    @BindView(R.id.exp_num)
    TextView expNum;
    @BindView(R.id.owner_name)
    TextView ownerName;

    public ExpShopRecyclerViewAdapter(@Nullable List<WebExpressionFolder> data) {
        super(R.layout.item_exp_shop, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, final WebExpressionFolder item) {
        helper.setText(R.id.exp_name,item.getName());
        helper.setText(R.id.exp_num,item.getCount() + "+");
        helper.setText(R.id.owner_name,item.getOwner());
        int imageViewArray[] = new int[]{R.id.image_1,R.id.image_2,R.id.image_3,R.id.image_4,R.id.image_5};
        ALog.d(item.getExpressionList().size());
        int num = 0;
        if (item.getExpressionList().size()<5){
            num = item.getExpressionList().size();
        }else {
            num = 5;
        }

        for (int i =0;i<num;i++){
            UIUtil.setImageToImageView(2,item.getExpressionList().get(i).getUrl(), (GifImageView) helper.getView(imageViewArray[i]));
        }
        //如果表情包数目小于5，则剩余的表情占位不显示
        for (int j = num;j< 5; j++){
            helper.getView(imageViewArray[j]).setVisibility(View.GONE);
            helper.getView(R.id.fl_image_5).setVisibility(View.GONE);
        }

        helper.getView(R.id.item_view).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpFolderDetailActivity.actionStart(UIUtil.getContext(),item.getDir());
            }
        });

    }
}
