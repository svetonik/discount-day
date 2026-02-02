package com.company.discountday.parser;

import com.company.discountday.model.Order;

import java.time.LocalDateTime;

public class OrderParser {

    private static final String DELIMITER = "\\|";
    private static final int TIME_INDEX = 0;
    private static final int COMPANY_INDEX = 1;
    private static final int AMOUNT_INDEX = 2;

    public Order parse(String line) {
        String[] parts = line.split(DELIMITER);

        LocalDateTime time = LocalDateTime.parse(parts[TIME_INDEX]);
        String company = parts[COMPANY_INDEX];
        int amountKg = Integer.parseInt(parts[AMOUNT_INDEX]);

        return new Order(time, company, amountKg);
    }
}
