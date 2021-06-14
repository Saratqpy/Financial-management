package com.example.financial_management;
public class Transaction {

    public Transaction(){

    }
    public String  detail;
    public boolean category = false;
    public long  time,date;
    public int money;

    public Transaction(int money,String detail, boolean category, long time, long date) {
        this.money = money ;
        this.detail = detail;
        this.category= category;
        this.time = time;
        this.date = date;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public boolean Category() {
        return category;
    }

    public void setCategory(boolean category) {
        this.category = category;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getdate() {
        return date;
    }

    public void setdate(long date) {
        this.date = date;
    }


}