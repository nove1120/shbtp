package com.nove1120.dto;

public class BookSelectParam {
    private int uid = 0; //发布者uid
    private String content; //包括书名 作者 出版社的混合模糊查询 基于MySql全文索引 最小长度为2
    private String ISBN;
    private String category; //图书分类字母代号
    private int grade = -1; //0全新 1轻微痕迹 2污损折痕 3破损缺页 4垃圾成色
    private int priceMin = -1;//最低价格
    private int priceMax = -1;//最高价格
    private String location;
    private int state=0; //0为查询在售书籍 1为下架
    private int uploadTime ; //该字段标识信息几天前上传 大于0生效
    private OrderByClause orderByClause;
    private int limitBidStart; //大于0生效 查询结果从该id起进行查找 用于综合排序分页
    private int limitStart; //limit跳过条数 默认0 用于排序时分页
    private int limitCount; //每次查询记录数 由服务端设置 客户端设置无效

    public enum OrderByClause{
        PRICE_ASC("price ASC"),PRICE_DESC("price DESC"),CREATE_TIME_DESC("create_time DESC");
        private final String clause;
        private OrderByClause(String clause){
            this.clause=clause;
        }
        public String getClause(){
            return clause;
        }

        @Override
        public String toString() {
            return clause;
        }
    }

    @Override
    public String toString() {
        return "BookSelectParam{" +
                "uid=" + uid +
                ", content='" + content + '\'' +
                ", ISBN='" + ISBN + '\'' +
                ", category='" + category + '\'' +
                ", grade=" + grade +
                ", priceMin=" + priceMin +
                ", priceMax=" + priceMax +
                ", location='" + location + '\'' +
                ", state=" + state +
                ", uploadTime=" + uploadTime +
                ", orderByClause=" + orderByClause +
                ", limitBidStart=" + limitBidStart +
                ", limitStart=" + limitStart +
                ", limitCount=" + limitCount +
                '}';
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(int uploadTime) {
        this.uploadTime = uploadTime;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getLimitCount() {
        return limitCount;
    }

    public void setLimitCount(int limitCount) {
        this.limitCount = limitCount;
    }

    public int getLimitBidStart() {
        return limitBidStart;
    }

    public void setLimitBidStart(int limitBidStart) {
        this.limitBidStart = limitBidStart;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getPriceMin() {
        return priceMin;
    }

    public void setPriceMin(int priceMin) {
        this.priceMin = priceMin;
    }

    public int getPriceMax() {
        return priceMax;
    }

    public void setPriceMax(int priceMax) {
        this.priceMax = priceMax;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public OrderByClause getOrderByClause() {
        return orderByClause;
    }

    public void setOrderByClause(OrderByClause orderByClause) {
        this.orderByClause = orderByClause;
    }
}
