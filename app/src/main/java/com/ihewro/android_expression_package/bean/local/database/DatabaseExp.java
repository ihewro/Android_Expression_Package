package com.ihewro.android_expression_package.bean.local.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/04
 *     desc   : 这个表缓存本地的表情图片的信息，这样不必每次都显示表情包都从内存中循环遍历
 *     version: 1.0
 * </pre>
 */
public class DatabaseExp extends LitePalSupport{

    private int id;//主键
    private String name;//图片名称
    private String url;//图片路径或者图片地址
    private String folderName;//目录的名称
    private DatabaseExpFolder databaseExpFolder;

    public DatabaseExp() {
    }

    public DatabaseExp(String name, String url, String folderName) {
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

    public DatabaseExpFolder getDatabaseExpFolder() {
        return databaseExpFolder;
    }

    public void setDatabaseExpFolder(DatabaseExpFolder databaseExpFolder) {
        this.databaseExpFolder = databaseExpFolder;
    }
}
