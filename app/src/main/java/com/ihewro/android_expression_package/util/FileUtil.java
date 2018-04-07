package com.ihewro.android_expression_package.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
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

    // 获取当前目录下所有的mp4文件
    public static List<String> GetImageFileNameList(String fileAbsolutePath) {
        List<String> vecFile = new Vector<String>();
        File file = new File(fileAbsolutePath);
        File[] subFile = file.listFiles();

        for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
            // 判断是否为文件夹
            if (!subFile[iFileLength].isDirectory()) {
                String filename = subFile[iFileLength].getName();
                // 判断是否为结尾
                //if (filename.trim().toLowerCase().endsWith(".mp4")) {
                    vecFile.add(filename);
                //}
            }
        }
        return vecFile;
    }

    /**
     * 保存到SD卡
     * @param filename
     * @param filecontent
     * @throws Exception
     */
    public static void saveToSDCard(String filename, String filecontent)throws Exception{
        try {
            //判断SDcard是否存在并且可读写
            if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                Toast.makeText(UIUtil.getContext(), "", Toast.LENGTH_SHORT).show();

                File file = new File(Environment.getExternalStorageDirectory(),filename);
                FileOutputStream outStream = new FileOutputStream(file);
                outStream.write(filecontent.getBytes());
                outStream.close();

            }else{
                Toast.makeText(UIUtil.getContext(), "", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(UIUtil.getContext(), "", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
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

    /**
     * 拷贝assets文件下文件到指定路径
     *
     * @param context
     * @param assetDir  源文件/文件夹
     */
    public static void copyAssets(Context context, String assetDir, String targetDir) {

        Method getSystem = null;
        try {
            getSystem = AssetManager.class.getDeclaredMethod("getSystem");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            AssetManager am = (AssetManager) getSystem.invoke(null);
           // am.get
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }



        if (TextUtils.isEmpty(assetDir) || TextUtils.isEmpty(targetDir)) {
            return;
        }
        String separator = File.separator;
        try {
            // 获取assets目录assetDir下一级所有文件以及文件夹
            String[] fileNames = context.getResources().getAssets().list(assetDir);
            // 如果是文件夹(目录),则继续递归遍历
            if (fileNames.length > 0) {
                File targetFile = new File(targetDir);
                if (!targetFile.exists() && !targetFile.mkdirs()) {
                    return;
                }
                for (String fileName : fileNames) {
                    copyAssets(context, assetDir + separator + fileName, targetDir + separator + fileName);
                }
            } else { // 文件,则执行拷贝
                copy(context, assetDir, targetDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
