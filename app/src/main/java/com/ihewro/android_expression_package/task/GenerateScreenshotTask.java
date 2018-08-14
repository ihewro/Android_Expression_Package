package com.ihewro.android_expression_package.task;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.ALog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.TaskListener;
import com.ihewro.android_expression_package.util.UIUtil;

import java.util.concurrent.ExecutionException;

import es.dmoral.toasty.Toasty;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/08/12
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GenerateScreenshotTask extends AsyncTask<Void,Void,Boolean> {

    private Activity activity;
    private String content;
    private MaterialDialog dialog;
    private TaskListener listener;
    private Expression expression;
    private Bitmap.Config[] configs
            = {Bitmap.Config.ALPHA_8, Bitmap.Config.ARGB_4444, Bitmap.Config.ARGB_8888, Bitmap.Config.RGB_565};

    public GenerateScreenshotTask(Activity activity, String content ,Expression expression, TaskListener listener) {
        this.activity = activity;
        this.content = content;
        this.listener = listener;
        this.expression = expression;
    }

    @Override
    protected void onPreExecute() {
        dialog  = new MaterialDialog.Builder(activity)
                .content("生成分享图片中……")
                .progress(true, 0)
                .progressIndeterminateStyle(true)
                .show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        View view = activity.getLayoutInflater().inflate(R.layout.share_one_sreenshot,null);
        View v = view.findViewById(R.id.item_view);
        ImageView headerImage = view.findViewById(R.id.headerImage);
        TextView date = view.findViewById(R.id.dateText);
        TextView content = view.findViewById(R.id.content);
        ALog.d("名称为" + expression.getName().substring(0,10));
        String text = expression.getName().substring(0,10);
        //设置布局内容
        Drawable imageFile = null;
        try {
            imageFile = Glide.with(activity).asDrawable()
                    .apply(RequestOptions.priorityOf(Priority.HIGH).onlyRetrieveFromCache(true))
                    .load(expression.getUrl())
                    .submit().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if(imageFile != null){
            headerImage.setImageDrawable(imageFile);
        }else {
            return false;
        }
        date.setText(text);
        content.setText(this.content);
        //生成截图
        v.setDrawingCacheEnabled(true);
        //measure()实际测量 自己显示在屏幕上的宽高 2个参数，int widthMeasureSpec 和 int heightMeasureSpec表示具体的测量规则。
        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        //确定View的大小和位置的,然后将其绘制出来
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        //调用getDrawingCache方法就可 以获得view的cache图片
        Bitmap bb = Bitmap.createBitmap(v.getDrawingCache());
        //系统把原来的cache销毁
        v.setDrawingCacheEnabled(false);


        // 保存bitmap到sd卡
        UIUtil.saveBitmapToSdCard(activity,bb,"头图/" + expression.getName());
        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        Toasty.success(activity, "保存成功", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
        listener.onFinish(true);
    }
}
