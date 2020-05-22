package com.nove1120.pojo;

public class BookOrder {
    private int oid;
    private int bid;
    private int sell_uid;
    private int buy_uid;
    private String consignee;
    private String phone_number;
    private String location;
    private String address;
    private String express_company;
    private String express_number;
    private int mode;
    private int state;

    @Override
    public String toString() {
        return "BookOrder{" +
                "oid=" + oid +
                ", bid=" + bid +
                ", sell_uid=" + sell_uid +
                ", buy_uid=" + buy_uid +
                ", consignee='" + consignee + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", location='" + location + '\'' +
                ", address='" + address + '\'' +
                ", express_company='" + express_company + '\'' +
                ", express_number='" + express_number + '\'' +
                ", mode=" + mode +
                ", state=" + state +
                '}';
    }

    public int getOid() {
        return oid;
    }

    public void setOid(int oid) {
        this.oid = oid;
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public int getSell_uid() {
        return sell_uid;
    }

    public void setSell_uid(int sell_uid) {
        this.sell_uid = sell_uid;
    }

    public int getBuy_uid() {
        return buy_uid;
    }

    public void setBuy_uid(int buy_uid) {
        this.buy_uid = buy_uid;
    }

    public String getConsignee() {
        return consignee;
    }

    public void setConsignee(String consignee) {
        this.consignee = consignee;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getExpress_company() {
        return express_company;
    }

    public void setExpress_company(String express_company) {
        this.express_company = express_company;
    }

    public String getExpress_number() {
        return express_number;
    }

    public void setExpress_number(String express_number) {
        this.express_number = express_number;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
