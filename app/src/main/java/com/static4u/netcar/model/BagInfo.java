package com.static4u.netcar.model;

import java.io.Serializable;

/**
 * 套餐
 */
public class BagInfo implements Serializable {
    private String id = "";
    // 名称
    private String name = "";
    // 介绍
    private String intro = "";
    // 价格
    private String price = "";
    // 保证金
    private String deposit = "";

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

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDeposit() {
        return deposit;
    }

    public void setDeposit(String deposit) {
        this.deposit = deposit;
    }
}
