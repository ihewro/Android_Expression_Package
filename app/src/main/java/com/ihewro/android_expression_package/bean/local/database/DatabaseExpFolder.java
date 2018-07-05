package com.ihewro.android_expression_package.bean.local.database;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/04
 *     desc   :
 *     version: 1.0
 * </pre>
 */
public class DatabaseExpFolder extends LitePalSupport {

    public static final int EXP_FOLDER_INVALID = -1;
    public static final int EXP_FOLDER_NORMAL = 1;
    public static final int EXP_FOLDER_DELETED = -2;


    /**
     * 1 表示表情包正常存在
     * -1 表示表情包无效，不是在app中删除的，会在我的表情包显示该项，但是不会显示具体的细节
     * -2 表情表情包已经删除，不会在[我的表情包]中显示该项
     */
    private int exist;
    private int count;//表情数目
    private int id;//主键
    @Column(unique = true, defaultValue = "")
    private String name;//表情包目录名称
    private String owner;//上传作者的名称
    private String ownerAvatar;//上传作者的头像
    private String createTime;//创建时间,时间戳
    private String updateTime;//更新时间，时间戳

    private List<DatabaseExp> databaseExpList = new ArrayList<>();

    public DatabaseExpFolder() {
    }

    public DatabaseExpFolder(int exist, int count, String name, String owner, String ownerAvatar, String createTime, String updateTime, List<DatabaseExp> databaseExpList) {
        this.exist = exist;
        this.count = count;
        this.name = name;
        this.owner = owner;
        this.ownerAvatar = ownerAvatar;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.databaseExpList = databaseExpList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DatabaseExp> getDatabaseExpList() {
        return databaseExpList;
    }

    public void setDatabaseExpList(List<DatabaseExp> databaseExpList) {
        this.databaseExpList = databaseExpList;
    }

    public static int getExpFolderInvalid() {
        return EXP_FOLDER_INVALID;
    }

    public static int getExpFolderNormal() {
        return EXP_FOLDER_NORMAL;
    }

    public static int getExpFolderDeleted() {
        return EXP_FOLDER_DELETED;
    }

    public int getExist() {
        return exist;
    }

    public void setExist(int exist) {
        this.exist = exist;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwnerAvatar() {
        return ownerAvatar;
    }

    public void setOwnerAvatar(String ownerAvatar) {
        this.ownerAvatar = ownerAvatar;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {

        return "id = " +id + '\n'
                +"count" + count + '\n'
                + "name" + name;
    }
}
