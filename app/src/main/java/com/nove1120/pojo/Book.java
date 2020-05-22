package com.nove1120.pojo;

public class Book {
    private int bid;
    private int uid;
    private String book_name;
    private String author;
    private String publisher;
    private String publishingTime;
    private String ISBN;
    private int data_from; //0-第三方数据源 1-手动输入
    private String category;
    private int grade; //描述图书新旧程度 新旧程度 0-未定义 1-全新 2-非全新无明显痕迹 3-有不影响阅读的折痕卷页或污渍、无缺页 4-有明显污渍、破损或缺页 5-垃圾成色
    private double price;
    private String location;
    private int state;
    private String create_time;
    private BookDesc bookDesc;

    @Override
    public String toString() {
        return "Book{" +
                "bid=" + bid +
                ", uid=" + uid +
                ", book_name='" + book_name + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", publishingTime='" + publishingTime + '\'' +
                ", ISBN='" + ISBN + '\'' +
                ", data_from=" + data_from +
                ", category='" + category + '\'' +
                ", grade=" + grade +
                ", price=" + price +
                ", location='" + location + '\'' +
                ", state=" + state +
                ", create_time='" + create_time + '\'' +
                ", bookDesc=" + bookDesc +
                '}';
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPublishingTime() {
        return publishingTime;
    }

    public void setPublishingTime(String publishingTime) {
        this.publishingTime = publishingTime;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public int getData_from() {
        return data_from;
    }

    public void setData_from(int data_from) {
        this.data_from = data_from;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public BookDesc getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(BookDesc bookDesc) {
        this.bookDesc = bookDesc;
    }
}
