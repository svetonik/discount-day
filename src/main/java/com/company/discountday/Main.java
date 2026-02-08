package com.company.discountday;

import com.company.discountday.manager.OrderManager;
import com.company.discountday.service.FileOrderService;
import com.company.discountday.service.OrderService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        FileOrderService fileService = new FileOrderService();
        OrderService orderService = new OrderService();

        OrderManager manager = new OrderManager(
                fileService,
                orderService
        );

        try {
            manager.processOrders(
                    "src/main/resources/orders.txt",
                    "src/main/resources/result.txt",
                    0.50,
                    0.05,
                    10
            );

            try (var lines = Files.lines(Path.of("src/main/resources/result.txt"))) {
                lines.forEach(System.out::println);
            }

            System.out.println();


            manager.processOrders(
                    "src/main/resources/orders",
                    "src/main/resources/result2.txt",
                    0.50,
                    0.05,
                    10
            );

            try (var lines = Files.lines(Path.of("src/main/resources/result2.txt"))) {
                lines.forEach(System.out::println);
            }


        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка при обработке заказов: " + e.getMessage());
        }
    }
}
