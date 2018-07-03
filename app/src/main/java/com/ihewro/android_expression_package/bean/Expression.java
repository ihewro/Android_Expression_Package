package com.ihewro.android_expression_package.bean;

import java.io.Serializable;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/04/06
 *     desc   : 单个表情信息类
 *     version: 1.0
 * </pre>
 */
public class Expression implements Serializable {

    private int status;//标志位，图片来源：-1 apk内置图片 1 sd卡图片 2 网络图片
    private String name;//图片名称
    private String url;//图片路径或者图片地址（本地/网络）
    private String folderName;//目录的名称

    public Expression(int status,String name, String url) {
        this.status = status;
        this.name = name;
        this.url = url;
    }

    public Expression() {
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }
}
