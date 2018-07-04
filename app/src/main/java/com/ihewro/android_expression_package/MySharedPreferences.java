package com.ihewro.android_expression_package;

import android.content.SharedPreferences;
import android.os.Environment;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ihewro.android_expression_package.bean.Expression;
import com.ihewro.android_expression_package.bean.ExpressionFolder;
import com.ihewro.android_expression_package.bean.local.LocalExpressionFolder;
import com.ihewro.android_expression_package.util.UIUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static android.content.Context.MODE_PRIVATE;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/04
 *     desc   : 持续化的数据管理
 *     version: 1.0
 * </pre>
 */
public class MySharedPreferences {

    @Deprecated
    private static final String PREF_STORAGE_EXP_FOLDER_LIST = "PREF_STORAGE_NAME_LIST";//SD 存储卡的表情包名称列表

    /**
     * 增加sd卡中的表情包记录
     * @param expFolderName 表情包名称
     *                      @deprecated  使用数据库存储方法
     *                      TODO:这个地方存储的信息还需要修改，连表情包的名字都没有存进来
     */
    public static void addStorageExpFolderName(String expFolderName,String ownerName,String ownerAvatar){
        SharedPreferences.Editor editor = UIUtil.getContext().getSharedPreferences(PREF_STORAGE_EXP_FOLDER_LIST, MODE_PRIVATE).edit();
        ObjectMapper mapper = new ObjectMapper();
        try {
            String expFolderInfo = mapper.writeValueAsString(new LocalExpressionFolder(ownerName,ownerAvatar,1));
            int keyCount = getPrefStorageExpFolderCount();//序号从0开始，便于后面删除
            editor.putString(String.valueOf(keyCount),expFolderInfo);//这个地方的key用序号，方便后面进行循环搜索
            editor.putInt("count", keyCount + 1);//表示sd卡的表情包数目
            editor.apply();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


    /**
     * 删除sd卡中的表情包记录
     * @param expFolderName 表情包名称
     * @deprecated  使用数据库存储方法
     */
    public static void deleteStorageExpName(String expFolderName){
        SharedPreferences.Editor editor = UIUtil.getContext().getSharedPreferences(PREF_STORAGE_EXP_FOLDER_LIST, MODE_PRIVATE).edit();
        editor.remove(expFolderName);
        editor.putInt("count", getPrefStorageExpFolderCount() -1);//表示sd卡的表情包数目
        editor.apply();
    }

    /**
     * 返回sd卡内的表情包数目
     * @return
     * @deprecated  使用数据库存储方法
     */
    public static int getPrefStorageExpFolderCount(){
        try {
            SharedPreferences preferences = UIUtil.getContext().getSharedPreferences(PREF_STORAGE_EXP_FOLDER_LIST, MODE_PRIVATE);
            return preferences.getInt("count",0);
        }catch (Exception e){
            return 0;
        }
    }


    /**
     * 获取用户下载到sd卡的表情包名称列表，还需要经过下面的getStorageExpFolderList方法才能正常完成
     * @return
     * @deprecated  使用数据库存储方法
     */
    private static List<LocalExpressionFolder> getStorageExpFolderNameList(){
        return null;
    }

    private static LocalExpressionFolder getExpFolderByCount(int num){
        SharedPreferences preferences = UIUtil.getContext().getSharedPreferences(PREF_STORAGE_EXP_FOLDER_LIST, MODE_PRIVATE);
        String jsonString =  preferences.getString(num + "","{}");
        ObjectMapper mapper = new ObjectMapper();
        try {
            LocalExpressionFolder expressionFolder = mapper.readValue(jsonString, LocalExpressionFolder.class);
            return expressionFolder;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 获取sd卡中的表情包目录列表，通过扫描sd卡的指定目录
     * @return
     * @deprecated  使用数据库存储方法
     */
    public static List<LocalExpressionFolder> getStorageExpFolderList(){
        String appDirPath = GlobalConfig.appDirPath;
        List<LocalExpressionFolder> expressionFolderList = new ArrayList<>();
        int count = getPrefStorageExpFolderCount();

        /* 表情包序号是从0开始的。*/
        for (int i = 0; i < count; i++){
            LocalExpressionFolder expressionFolder = getExpFolderByCount(i);
            if (expressionFolder != null){
                String expFolderName = expressionFolder.getName();
                String expFolderPath = appDirPath + expFolderName;//表情包目录的地址
                File file = new File(expFolderPath);
                if (file.exists() && file.isDirectory()){//如果是表情包目录
                    expressionFolder.setExpressionList(getStorageExpList(file,expFolderName,5));

                }else {//sd卡中不存在这个文件夹，说明人为删除了，或者由于不可逆的原因导致的表情包丢失，将这个表情包标记的-1
                    try {
                        expressionFolder.setExist(LocalExpressionFolder.EXP_FOLDER_INVALID);
                        SharedPreferences.Editor editor = UIUtil.getContext().getSharedPreferences(PREF_STORAGE_EXP_FOLDER_LIST, MODE_PRIVATE).edit();
                        ObjectMapper mapper = new ObjectMapper();
                        String jsonString = mapper.writeValueAsString(expressionFolder);
                        editor.putString(String.valueOf(i),jsonString);
                        editor.apply();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    //给本地存储信息的目录标记为无效
                }
                expressionFolderList.add(expressionFolder);//就算状态为-1和-2的表情包信息也放到这个列表里，为的是方便后面删除表情包信息。
            }else {
                Toasty.error(UIUtil.getContext(),"程序配置信息出现异常错误，正在清空所有配置，请谅解", Toast.LENGTH_SHORT).show();
            }
        }
        return expressionFolderList;
    }

    /**
     * 获取某个表情包的所有表情信息
     *
     * @param expFolderName 表情包目录地址
     * @param count 获取的表情数目，不限制则为-1
     * @return
     * @deprecated  使用数据库存储方法
     */
    public static List<Expression> getStorageExpList(File dirFile,String expFolderName,int count){
        String expFolderPath = GlobalConfig.appDirPath + expFolderName;//表情包目录的地址

        List<Expression> expressionList = new ArrayList<>();//当前表情包目录下的表情列表
        String[] expNameArray = dirFile.list();
        if (count == -1){
            count = expNameArray.length;
        }
        for (int j = 0; j < UIUtil.getMinInt(expNameArray.length,count); j++){
            Expression tempExpression = new Expression(1,expNameArray[j],expFolderPath + "/" + expNameArray[j],expFolderName);
            expressionList.add(tempExpression);
        }

        return expressionList;
    }
}
