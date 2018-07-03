package com.ihewro.android_expression_package;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/02
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class GlobalConfig {

    public static final String assetsFolderName = "imagehuyi";//apk内置的所有表情包目录
    public static final String storageFolderName = "expressionBaby";//所有表情包都在此目录下建立子目录
    public static final String serverUrl = "http://192.168.43.73/exp/";
    public static final String getDirListUrl = serverUrl + "expFolderList.php";
    public static final String getDirDetailUrl = serverUrl + "expFolderDetail.php";

}
