package com.static4u.netcar.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CarInfo implements Serializable {
    private int pIndex = 0;

    public int getpIndex() {
        return pIndex;
    }

    public void setpIndex(int pIndex) {
        this.pIndex = pIndex;
    }

    // ID
    private String id = "";
    // 名称
    private String name = "";
    // 来源
    private String company = "";
    // 价格
    private String price = "";
    // 价格（优惠前）
    private String priceOld = "";
    // 简介
    private String sub = "";
    // 车型
    private String type = "";
    // 照片
    private List<String> imgList = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPriceOld() {
        return priceOld;
    }

    public void setPriceOld(String priceOld) {
        this.priceOld = priceOld;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }
}
