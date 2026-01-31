package com.company.discountday.model;

import java.math.BigDecimal;

public class OrderResult {

    private String company;
    private BigDecimal finalPrice;

    public OrderResult(String company, BigDecimal finalPrice) {
        this.company = company;
        this.finalPrice = finalPrice;
    }

    public String getCompany() {
        return company;
    }

    public BigDecimal getFinalPrice() {
        return finalPrice;
    }
}
