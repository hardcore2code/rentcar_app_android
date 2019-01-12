package com.static4u.netcar.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 车型
 */
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
    // 品牌
    private String brand = "";
    // 厂商
    private String company = "";
    // 价格
    private String price = "";
    // 价格（优惠前）
    private String priceOld = "";
    // 简介
    private String sub = "";
    // 车型
    private String type = "";
    // 动力类型
    private String power = "";
    // 座位数
    private String sit = "";
    // 里程
    private String length = "";
    // 重量
    private String weight = "";
    // 充电时间
    private String charge = "";
    // 地址1
    private String loc1 = "";
    // 地址2
    private String loc2 = "";
    // 经度
    private double longitude = 117.20;
    // 纬度
    private double latitude = 39.12;
    // 联系电话
    private String phone = "";

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    // 评价
    private List<CommentInfo> commList = new ArrayList<>();
    // 照片
    private List<String> imgList = new ArrayList<>();

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

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

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getSit() {
        return sit;
    }

    public void setSit(String sit) {
        this.sit = sit;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public String getLoc1() {
        return loc1;
    }

    public void setLoc1(String loc1) {
        this.loc1 = loc1;
    }

    public String getLoc2() {
        return loc2;
    }

    public void setLoc2(String loc2) {
        this.loc2 = loc2;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public List<CommentInfo> getCommList() {
        return commList;
    }

    public void setCommList(List<CommentInfo> commList) {
        this.commList = commList;
    }
}
