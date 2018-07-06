package com.ihewro.android_expression_package.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import com.blankj.ALog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.bean.Expression;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

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
    public static void updateMediaStore(final Context context, final String path) {
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


    /***
     * 删除指定文件夹下所有文件
     *
     * @param path 文件夹完整绝对路径
     * @return
     */
    public static  boolean delAllFile(String path) {
        boolean flag = false;
        File file = new File(path);
        if (!file.exists()) {
            return flag;
        }
        if (!file.isDirectory()) {
            return flag;
        }
        String[] tempList = file.list();
        File temp = null;
        for (int i = 0; i < tempList.length; i++) {
            if (path.endsWith(File.separator)) {
                temp = new File(path + tempList[i]);
            } else {
                temp = new File(path + File.separator + tempList[i]);
            }
            if (temp.isFile()) {
                temp.delete();
            }
            if (temp.isDirectory()) {
                delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
                delFolder(path + "/" + tempList[i]);// 再删除空文件夹
                flag = true;
            }
        }
        return flag;
    }

    /***
     * 删除文件夹
     *
     * @param folderPath 文件夹完整绝对路径
     */
    public  static void delFolder(String folderPath) {
        try {
            delAllFile(folderPath); // 删除完里面所有内容
            File myFilePath = new File(folderPath);
            ALog.d("删除后",myFilePath.list().length);
            myFilePath.delete(); // 删除空文件夹
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static boolean copyFileToTarget(String origin,String target){
        if (Objects.equals(origin, target)){//如果路径相同，直接返回true,否则会发生crash，因为一个文件不能打开的同时边读边写
            return true;
        }else {
            return copyFileToTarget(new File(origin),new File(target));
        }
    }

    public static boolean copyFileToTarget(File source,File target){
        if (Objects.equals(source.getAbsolutePath(), target.getAbsolutePath())){
            return true;
        }else {
            File fileParent = target.getParentFile();//如果表情包目录都不存在，则需要先创建目录
            if(!fileParent.exists()){
                fileParent.mkdirs();
            }
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                fileInputStream = new FileInputStream(source);
                fileOutputStream = new FileOutputStream(target);
                byte[] buffer = new byte[1024];
                while (fileInputStream.read(buffer) > 0) {
                    fileOutputStream.write(buffer);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                try {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
            return true;
        }
    }

}
