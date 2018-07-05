package com.ihewro.android_expression_package.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/04
 *     desc   : 这个表缓存本地的表情图片的信息，这样不必每次都显示表情包都从内存中循环遍历
 *     notice  : 注意，这里我为了偷懒，减少数据转化，直接Expression这个类拿来用了，好处是不用再转换数据了，坏处是，这里面继承了LitePalSupport，导致额外的一些变量，但是不影响我们的使用
 *     version: 1.0
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Expression extends LitePalSupport{

    private int id;//主键
    private int status;//标志位，图片来源：~~-1 apk内置图片~~ 1 sd卡图片 2 网络图片
    private String name;//图片名称
    private String url;//图片路径或者图片地址
    private String folderName;//目录的名称
    private ExpressionFolder expressionFolder;


    public Expression() {
    }

    public Expression(int status, String name, String url, String folderName) {
        this.status = status;
        this.name = name;
        this.url = url;
        this.folderName = folderName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public ExpressionFolder getExpressionFolder() {
        return expressionFolder;
    }

    public void setExpressionFolder(ExpressionFolder expressionFolder) {
        this.expressionFolder = expressionFolder;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
