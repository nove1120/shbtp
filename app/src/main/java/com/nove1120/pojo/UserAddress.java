package com.nove1120.pojo;

public class UserAddress {

    private int address_id;
    private int uid;
    private String consignee;
    private String phone_number;
    private String location;
    private String address;
    private int default_flag;

    @Override
    public String toString() {
        return "Address{" +
                "address_id=" + address_id +
                ", uid=" + uid +
                ", consignee='" + consignee + '\'' +
                ", phone_number='" + phone_number + '\'' +
                ", location='" + location + '\'' +
                ", address='" + address + '\'' +
                ", default_flag=" + default_flag +
                '}';
    }

    public int getDefault_flag() {
        return default_flag;
    }

    public void setDefault_flag(int default_flag) {
        this.default_flag = default_flag;
    }

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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
}
