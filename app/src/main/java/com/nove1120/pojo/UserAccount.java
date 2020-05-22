package com.nove1120.pojo;

public class UserAccount {
    @Override
    public String toString() {
        return "UserAccount{" +
                "uid=" + uid +
                ", password='" + password + '\'' +
                '}';
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    long uid;
    String password;


}
