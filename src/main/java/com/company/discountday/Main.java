package com.company.discountday;

import com.company.discountday.service.OrderManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {

        OrderManager manager = new OrderManager();

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

        } catch (IOException e) {
            System.err.println("Ошибка при работе с файлами: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Ошибка при обработке заказов: " + e.getMessage());
        }
    }
}
