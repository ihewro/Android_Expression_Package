package com.ihewro.android_expression_package.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.litepal.crud.LitePalSupport;

import java.util.List;

/**
 * <pre>
 *     author : hewro
 *     e-mail : ihewro@163.com
 *     time   : 2018/07/06
 *     desc   : 这个表中，永远只存一条数据
 *     version: 1.0
 * </pre>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OneDetailList extends LitePalSupport {
    private String date;//存储时间，用来判断是否需要更新数据
    private int count;//数目
    private List<OneDetail> oneDetailList;

    public OneDetailList() {
    }

    public OneDetailList(String date, int count, List<OneDetail> oneDetailList) {
        this.date = date;
        this.count = count;
        this.oneDetailList = oneDetailList;
    }

    public OneDetailList(String date, int count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<OneDetail> getOneDetailList() {
        return oneDetailList;
    }

    public void setOneDetailList(List<OneDetail> oneDetailList) {
        this.oneDetailList = oneDetailList;
    }


}
