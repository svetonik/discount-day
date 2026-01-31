package com.company.discountday.service;

import com.company.discountday.model.Order;

import java.time.LocalDateTime;

public class OrderParser {

    public Order parse(String line) {
        String[] parts = line.split("\\|");

        LocalDateTime time = LocalDateTime.parse(parts[0]);
        String company = parts[1];
        int amountKg = Integer.parseInt(parts[2]);

        return new Order(time, company, amountKg);
    }
}
