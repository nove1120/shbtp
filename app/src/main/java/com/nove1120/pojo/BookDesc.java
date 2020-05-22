package com.nove1120.pojo;

public class BookDesc {
    private int bid;
    private String text_desc;//文本描述
    private String picture_url;//图片相对路径 最多9图 多url之间使用$分隔

    @Override
    public String toString() {
        return "BookDesc{" +
                "bid=" + bid +
                ", text_desc='" + text_desc + '\'' +
                ", picture_url='" + picture_url + '\'' +
                '}';
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }


    public String getText_desc() {
        return text_desc;
    }

    public void setText_desc(String text_desc) {
        this.text_desc = text_desc;
    }

    public void setPicture_url(String picture_url) {
        this.picture_url = picture_url;
    }

    public String getPicture_url() {
        return picture_url;
    }

    public String[] getPicture_urlArray(){
        return picture_url.split("\\$");
    }
    public String getPicture_urlIndex(int index){
        String[] urls = picture_url.split("$");
        return urls[0];
    }
}