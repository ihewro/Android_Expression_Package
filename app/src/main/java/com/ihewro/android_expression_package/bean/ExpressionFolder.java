package com.ihewro.android_expression_package.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.litepal.LitePal;
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
 *     notice  : 注意，这里我为了偷懒，减少数据转化，直接ExpressionFolder这个类拿来用了，好处是不用再转换数据了，坏处是，这里面继承了LitePalSupport，导致额外的一些变量，但是不影响我们的使用
 *     version: 1.0
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpressionFolder extends LitePalSupport {

    public static final int EXP_FOLDER_INVALID = -1;
    public static final int EXP_FOLDER_NORMAL = 1;
    public static final int EXP_FOLDER_DELETED = -2;


    /**
     * 1 表示表情包正常存在
     * -1 表示表情包无效，不是在app中删除的，会在我的表情包显示该项，但是不会显示具体的细节
     * -2 表情表情包已经删除，不会在[我的表情包]中显示该项
     */
    private int exist;//本地表情包使用的存在位
    private int count;//表情数目
    private int id;//本地做主键
    @Column(unique = true, defaultValue = "")
    private String name;//表情包目录名称
    private String owner;//上传作者的名称
    private String ownerAvatar;//上传作者的头像
    private String createTime;//创建时间,时间戳
    private String updateTime;//更新时间，时间戳
    private int dir;//网络层的目录id，只在网络层中找到表情包目录对应的表情列表有用，存到本地数据库的时候可以随便存储一个值，这个不需要的

    private List<Expression> expressionList;

    public ExpressionFolder() {
    }

    public ExpressionFolder(int exist, int count, String name, String owner, String ownerAvatar, String createTime, String updateTime, List<Expression> expressionList,int dir) {
        this.exist = exist;
        this.count = count;
        this.name = name;
        this.owner = owner;
        this.ownerAvatar = ownerAvatar;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.expressionList = expressionList;
        this.dir = dir;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Expression> getExpressionList() {
        if (expressionList == null){
            return new ArrayList<Expression>();
        }else {
            return expressionList;
        }
    }


    public List<Expression> getExpressionList(boolean data) {
        if (!data){
            return LitePal.select("id","name","foldername","status","url","expressionfolder_id","desstatus","description").where("foldername = ?",name).find(Expression.class,true);
        }else {
            return LitePal.where("foldername =?",name).find(Expression.class,true);//连图片数据也查询出来
        }
    }

    public void setExpressionList(List<Expression> expressionList) {
        this.expressionList = expressionList;
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

    public int getDir() {
        return dir;
    }

    public void setDir(int dir) {
        this.dir = dir;
    }

    @Override
    public String toString() {

        return "id = " +id + '\n'
                +"count" + count + '\n'
                + "name" + name;
    }
}
