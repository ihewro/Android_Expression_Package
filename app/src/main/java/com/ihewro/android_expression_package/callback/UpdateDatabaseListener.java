package com.ihewro.android_expression_package.callback;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/05
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public interface UpdateDatabaseListener {


    public void onFinished();//更新结束

    public void onProgress(int progress,int all);//更新过程中

    public void onStart();//开始执行，显示一个进度条对话框

}
