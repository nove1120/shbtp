package com.nove1120.dto;

public class LoginParam {
    private String account;
    private AccountType accountType;
    private String password;

    public enum AccountType{
        USER_NAME,PHONE_NUMBER,EMAIL
    }

    @Override
    public String toString() {
        return "LoginParam{" +
                "account='" + account + '\'' +
                ", accountType=" + accountType +
                ", password='" + password + '\'' +
                '}';
    }

    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
