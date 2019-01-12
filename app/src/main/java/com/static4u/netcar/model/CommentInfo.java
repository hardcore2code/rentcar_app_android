package com.static4u.netcar.model;

import java.io.Serializable;

/**
 * 评价
 */
public class CommentInfo implements Serializable {
    private String id = "";
    private String name = "";
    private String time = "";
    private String header = "";
    private String content = "";

    public CommentInfo(String name, String time, String header, String content) {
        this.name = name;
        this.time = time;
        this.header = header;
        this.content = content;
    }

    public CommentInfo() {
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
