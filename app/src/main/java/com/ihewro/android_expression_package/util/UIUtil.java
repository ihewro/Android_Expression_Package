package com.ihewro.android_expression_package.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestOptions;
import com.ihewro.android_expression_package.MyApplication;
import com.ihewro.android_expression_package.R;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.callback.TaskListener;

import org.litepal.LitePal;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/04/06
 *     desc   : 全局操作的一些公共操作
 *     version: 1.0
 * </pre>
 */
public class UIUtil {

    final static int BUFFER_SIZE = 4096;


    /**
     * 获取全局Context，静态方法，你可以在任何位置调用该方法获取Context
     * @return
     */
    public static Context getContext() {
        return MyApplication.getContext();
    }

    /**
     * 获取资源对象
     *
     * @return
     */
    public static Resources getResources() {
        return getContext().getResources();
    }

    /**
     * 获取资源文件字符串
     *
     * @param resId
     * @return
     */
    public static String getString(int resId) {
        return getResources().getString(resId);
    }

    /**
     * 获取资源文件字符串数组
     *
     * @param resId
     * @return
     */
    public static String[] getStringArray(int resId) {
        return getResources().getStringArray(resId);
    }

    /**
     * 获取资源文件颜色
     *
     * @param resId
     * @return
     */
    public static int getColor(int resId) {
        return getResources().getColor(resId);
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    public static void setImageToImageView(Expression expression,ImageView imageView){

        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading)
                .error(R.drawable.fail);

        switch (expression.getStatus()){
            case 1:
                List<Expression>tempExpList = LitePal.where("name =? and foldername =?",expression.getName(),expression.getFolderName()).find(Expression.class);
                Glide.with(UIUtil.getContext()).load(tempExpList.get(0).getImage().getContent()).apply(options).transition(withCrossFade()).into(imageView);
                break;
            case 2:
                Glide.with(UIUtil.getContext()).load(expression.getUrl()).apply(options).transition(withCrossFade()).into(imageView);
                break;
        }

    }

    public static void setImageToImageView(int status, String url, ImageView imageView){
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.loading)
                .error(R.drawable.fail);

        switch (status){
            case 1://存储在sd卡中
                //获取路径
                //本地文件
                File file = new File(url);
                //加载图片
                Glide.with(UIUtil.getContext()).load(file).apply(options).transition(withCrossFade()).into(imageView);
                break;

            case 2://加载网络地址
                Glide.with(UIUtil.getContext()).load(url).apply(options).transition(withCrossFade())
                .into(imageView);
                break;
        }

    }

    // InputStream转换成Drawable
    public static Drawable InputStream2Drawable(InputStream is) {
        Bitmap bitmap = InputStream2Bitmap(is);
        return bitmap2Drawable(bitmap);
    }

    // 将InputStream转换成Bitmap
    public static Bitmap InputStream2Bitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }

    // Bitmap转换成Drawable
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        BitmapDrawable bd = new BitmapDrawable(bitmap);
        Drawable d = (Drawable) bd;
        return d;
    }

    // Drawable转换成Bitmap
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(  0,   0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 将InputStream转换成byte数组
     * @param in
     * @return
     * @throws IOException
     */
    public static byte[] InputStreamTOByte(InputStream in) throws IOException{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int count = -1;
        while((count = in.read(data,0,BUFFER_SIZE)) != -1)
            outStream.write(data, 0, count);

        data = null;
        return outStream.toByteArray();
    }

    // Bitmap转换成byte[]
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }


    public static int getMinInt(int var1,int var2){
        if (var1 < var2){
            return var1;
        }else {
            return var2;
        }
    }

    public static void goodEgg(int times, TaskListener listener){
        switch (times) {
            case 3:
                ToastUtil.showMessageShort("还戳！！！");
                break;

            case 10:
                ToastUtil.showMessageShort("好玩吗");
                break;

            case 20:
                ToastUtil.showMessageShort("很无聊？");
                break;

            case 40:
                ToastUtil.showMessageShort("。。。");
                break;

            case 50:
                ToastUtil.showMessageShort("其实我是一个炸弹💣");
                break;

            case 60:
                ToastUtil.showMessageShort("是不是吓坏了哈哈，骗你的");
                break;

            case 70:
                ToastUtil.showMessageShort("看你还能坚持多久");
                break;

            case 90:
                ToastUtil.showMessageShort("哇！！！就问你手指痛吗");
                break;

            case 110:
                ToastUtil.showMessageShort("其实，生活还有很多有意义的事情做，比如。。。。");
                break;

            case 120:
                ToastUtil.showMessageShort("比如找我聊天啊，别戳了喂");
                break;

            case 130:
                ToastUtil.showMessageShort("去找我聊天吧，用我的表情包，哈哈哈哈哈");
                break;

            case 140:
                ToastUtil.showMessageShort("我走了，祝你玩得开心");
                break;

            case 150:
                ToastUtil.showMessageShort("哈哈哈，其实我没走哦，看你这么努力，告诉你一个秘密");
                break;

            case 160:
                ToastUtil.showMessageShort("我喜欢你( *︾▽︾)，这次真的要再见了哦👋，再见");
                listener.onFinish(true);
                break;

        }
    }



}
