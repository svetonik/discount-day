package com.company.discountday.service;

import com.company.discountday.model.Order;
import com.company.discountday.model.OrderResult;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class OrderManager {

    private final FileOrderService fileService = new FileOrderService();
    private final OrderParser parser = new OrderParser();
    private final OrderService orderService = new OrderService();

    public void processOrders(
            String inputPath,
            String outputPath,
            double startDiscount,
            double discountStep,
            double pricePerKg
    ) throws IOException {

        List<String> lines = fileService.readFile(inputPath);

        List<Order> orders = lines.stream()
                .map(parser::parse)
                .collect(Collectors.toList());

        List<OrderResult> results = orderService.calculateResults(
                orders,
                startDiscount,
                discountStep,
                pricePerKg
        );

        List<String> outputLines = results.stream()
                .map(r -> r.getCompany() + " - " + r.getFinalPrice())
                .collect(Collectors.toList());

        fileService.writeFile(outputPath, outputLines);
    }
}
