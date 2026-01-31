package com.company.discountday.model;

import java.time.LocalDateTime;

public class Order {

    private LocalDateTime time;
    private String company;
    private int amountKg;

    public Order(LocalDateTime time, String company, int amountKg) {
        this.time = time;
        this.company = company;
        this.amountKg = amountKg;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public String getCompany() {
        return company;
    }

    public int getAmountKg() {
        return amountKg;
    }
}
