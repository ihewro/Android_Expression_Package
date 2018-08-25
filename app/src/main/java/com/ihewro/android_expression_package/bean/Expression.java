package com.ihewro.android_expression_package.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ihewro.android_expression_package.GlobalConfig;
import com.ihewro.android_expression_package.util.DateUtil;
import com.ihewro.android_expression_package.util.FileUtil;
import com.ihewro.android_expression_package.util.UIUtil;

import org.litepal.LitePal;
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
    private int status;//标志位，图片来源：~~-1 apk内置图片~~ 1 数据库图片 2 网络图片 3 本机图片（数据库中没有存，头图分享卡片就是这种类型）
    private String name;//图片名称
    private String url;//图片路径或者图片地址
    private String folderName;//目录的名称
    private String description;//图片描述
    private int desStatus;//是否有图片描述，1为有，0为无
    private byte[] image;//图片内容，二进制存储


    public Expression() {
    }




    //构造方法里面增加了ExpressionFolder，避免某些情况，无法自动关联外键的情况，快被这个外键折腾疯了
    public Expression(int status, String name, String url, String folderName) {
        this.status = status;
        this.name = name;
        this.url = url;
        this.folderName = folderName;
    }

    public Expression(int status, String name, String url, String folderName,byte[] image) {
        this.status = status;
        this.name = name;
        this.url = url;
        this.folderName = folderName;
        this.image = image;
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


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDesStatus() {
        return desStatus;
    }

    public void setDesStatus(int desStatus) {
        this.desStatus = desStatus;
    }

    public byte[] getImage() {
        return image;
    }

    public byte[] getImage(boolean is) {
        LitePal.find(Expression.class,this.id).getImage();
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
