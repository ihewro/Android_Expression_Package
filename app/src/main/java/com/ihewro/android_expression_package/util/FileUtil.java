package com.ihewro.android_expression_package.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import com.blankj.ALog;
import com.canking.minipay.MiniPayUtils;
import com.ihewro.android_expression_package.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

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
    public static void deleteImageFromGallery(String file){
        File file1 = new File(file);
        if (file1.exists() || file != ""){
            new File(file).delete();
            updateMediaStore(UIUtil.getContext(),file);
        }
    }

    /**
     * 更新图片库
     * @param context
     * @param path
     */
    public static void updateMediaStore(final Context context, final String path) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));

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
                String tempPath = temp.getAbsolutePath();
                temp.delete();
                updateMediaStore(UIUtil.getContext(),tempPath);
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
        if (Objects.equals(origin,target)){
            copyFileToTarget(origin,origin+"copy");
            deleteImageFromGallery(origin);
            copyFileToTarget(origin+"copy",target);
            deleteImageFromGallery(origin+"copy");
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
