package com.ihewro.android_expression_package.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/04/06
 *     desc   : 文件操作类
 *     version: 1.0
 * </pre>
 */
public class FileUtil {


    /**
     *
     * @param context
     * @param drawable
     * @param imageUrl
     * @param dirName
     * @param fileName
     * @param origin 保存来源，1表示直接保存，2表示保存后需要删除
     * @return
     */
    public static File saveImageToGallery(Context context, Drawable drawable, String imageUrl,String dirName, String fileName, int origin) {
        //保存文件前是否已经保存。-1表示未保存
        int status = -1;

        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory() + "/expressionBaby/" + dirName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File file = new File(appDir, fileName);
        if (file.exists()){
            status = 1;
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            InputStream is = UIUtil.getContext().getAssets().open(imageUrl);
            byte[] bytes = UIUtil.InputStreamTOByte(is);
            fos.write(bytes);
            fos.flush();
            fos.close();
            if (origin == 1){
                Toast.makeText(UIUtil.getContext(),"保存到" + Environment.getExternalStorageDirectory() + "expressionBaby/" + dirName + "/" +fileName,Toast.LENGTH_SHORT).show();
            }
            updateMediaStore(UIUtil.getContext(),file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(UIUtil.getContext(),"保存失败，请重试",Toast.LENGTH_SHORT).show();
        }

        if (status == -1 && origin == 2){//如果直接图片没有保存，则需要分享图片后删除掉
            deleteImageFromGallery(file);
        }

        return file;
    }

    /**
     * 从图片库中删除图片
     */
    public static void deleteImageFromGallery(File file){
        String filePath =file.getAbsolutePath();
        file.delete();
        updateMediaStore(UIUtil.getContext(),filePath);
    }

    /**
     * 更新图片库
     * @param context
     * @param path
     */
    public static void updateMediaStore(final  Context context, final String path) {
        //版本号的判断  4.4为分水岭，发送广播更新媒体库
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            MediaScannerConnection.scanFile(context, new String[]{path}, null, new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    mediaScanIntent.setData(uri);
                    context.sendBroadcast(mediaScanIntent);
                }
            });
        } else {
            File file = new File(path);
            String relationDir = file.getParent();
            File file1 = new File(relationDir);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(file1.getAbsoluteFile())));
        }
        //Toast.makeText(UIUtil.getContext(),"图库更新成功",Toast.LENGTH_SHORT).show();
    }


    /**
     * 复制文件
     *
     * @param context 上下文对象
     */
    public static void copy(Context context, String zipPath, String targetPath) {
        if (TextUtils.isEmpty(zipPath) || TextUtils.isEmpty(targetPath)) {
            return;
        }
        File dest = new File(targetPath);
        dest.getParentFile().mkdirs();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new BufferedInputStream(context.getAssets().open(zipPath));
            out = new BufferedOutputStream(new FileOutputStream(dest));
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



}
