package com.ihewro.android_expression_package.bean;

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
public class OneDetailList {
    private String data;//存储时间，用来判断是否需要更新数据
    private int count;//数目
    private List<OneDetail> oneDetailList;

    public OneDetailList(String data, int count, List<OneDetail> oneDetailList) {
        this.data = data;
        this.count = count;
        this.oneDetailList = oneDetailList;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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
