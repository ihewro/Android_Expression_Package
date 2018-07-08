package com.ihewro.android_expression_package.bean;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/08
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class Version {

    private boolean update;//是否有更新
    private boolean assets;// 是否有下载资源
    private int latestCode;//最新版本的versionCode
    private String versionName;//版本名称
    private String publishTime;//发布时间
    private String updateText;
    private String url;//下载地址

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public int getLatestCode() {
        return latestCode;
    }

    public void setLatestCode(int latestCode) {
        this.latestCode = latestCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public boolean isAssets() {
        return assets;
    }

    public void setAssets(boolean assets) {
        this.assets = assets;
    }

    public String getUpdateText() {
        return updateText;
    }

    public void setUpdateText(String updateText) {
        this.updateText = updateText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "isUpdate" + update
                + "versionName" + versionName;



    }
}
