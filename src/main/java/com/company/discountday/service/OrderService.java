package com.company.discountday.service;

import com.company.discountday.model.Order;
import com.company.discountday.model.OrderResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class OrderService {

    public List<OrderResult> calculateResults(
            List<Order> orders,
            double startDiscount,
            double discountStep,
            double pricePerKg
    ) {
        List<OrderResult> results = new ArrayList<>();

        orders.sort(Comparator.comparing(Order::getTime));

        double currentDiscount = startDiscount;

        for (Order order : orders) {

            BigDecimal amount = BigDecimal.valueOf(order.getAmountKg());
            BigDecimal price = BigDecimal.valueOf(pricePerKg);
            BigDecimal multiplier = BigDecimal.valueOf(1 - currentDiscount);

            BigDecimal finalPrice = amount
                    .multiply(price)
                    .multiply(multiplier)
                    .setScale(2, RoundingMode.HALF_UP);

            results.add(new OrderResult(order.getCompany(), finalPrice));

            currentDiscount -= discountStep;
            if (currentDiscount < 0) {
                currentDiscount = 0;
            }
        }

        return results;
    }
}
