package com.ihewro.android_expression_package.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/04/07
 *     desc   : 申请权限管理类
 *     version: 1.0
 * </pre>
 */
public final class CheckPermissionUtils {
    private CheckPermissionUtils() {
    }

    /**
     * 在这里数组中添加你需要申请的权限名称
     */
    private static String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };

    /**
     * 初始化检查基本权限是否已经申请成功
     *
     * @param context 全局Context
     * @return 未申请的权限List
     */
    public static String[] checkPermission(Context context){
        List<String> data = new ArrayList<>();//存储未申请的权限
        for (String permission : permissions) {
            int checkSelfPermission = ContextCompat.checkSelfPermission(context, permission);
            if(checkSelfPermission == PackageManager.PERMISSION_DENIED){//未申请
                data.add(permission);
            }
        }
        return data.toArray(new String[data.size()]);
    }

    /**
     * 调用方自定义自定义权限列表
     *
     * @param context 全局Context
     * @param permissions 需要申请的权限数组
     * @return 未申请的权限List
     */
    public static String[] checkPermission(Context context,String[]  permissions){

        List<String> data = new ArrayList<>();//存储未申请的权限
        for (String permission : permissions) {
            int checkSelfPermission = ContextCompat.checkSelfPermission(context, permission);
            if(checkSelfPermission == PackageManager.PERMISSION_DENIED){//未申请
                data.add(permission);
            }
        }
        return data.toArray(new String[data.size()]);
    }
}