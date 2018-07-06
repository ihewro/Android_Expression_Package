package com.ihewro.android_expression_package.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.litepal.crud.LitePalSupport;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/06
 *     desc   : 一个 详情
 *     version: 1.0
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneDetail extends LitePalSupport {
    private String text;
    private String imgUrl;
    private OneDetailList oneDetailList;

    public OneDetail() {
    }

    public OneDetail(String text, String imgUrl) {
        this.text = text;
        this.imgUrl = imgUrl;
    }

    public OneDetail(String text, String imgUrl, OneDetailList oneDetailList) {
        this.text = text;
        this.imgUrl = imgUrl;
        this.oneDetailList = oneDetailList;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public OneDetailList getOneDetailList() {
        return oneDetailList;
    }

    public void setOneDetailList(OneDetailList oneDetailList) {
        this.oneDetailList = oneDetailList;
    }
}
