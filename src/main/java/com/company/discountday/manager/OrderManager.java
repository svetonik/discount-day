package com.company.discountday.manager;

import com.company.discountday.model.Order;
import com.company.discountday.model.OrderResult;
import com.company.discountday.service.FileOrderService;
import com.company.discountday.parser.OrderParser;
import com.company.discountday.parser.ParserFactory;
import com.company.discountday.service.OrderService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class OrderManager {

    private final FileOrderService fileService;
    private final OrderService orderService;

    public OrderManager(
            FileOrderService fileService,
            OrderService orderService
    ) {
        this.fileService = fileService;
        this.orderService = orderService;
    }

    public void processOrders(
            String inputPath,
            String outputPath,
            double startDiscount,
            double discountStep,
            double pricePerKg
    ) throws IOException {

        OrderParser parser = ParserFactory.createParser(inputPath);

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