package com.company.discountday.service;

import com.company.discountday.model.Order;
import com.company.discountday.model.OrderResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderServiceTest {

    @Test
    void testPriceCalculationWithDiscounts() {

        OrderService service = new OrderService();

        List<Order> orders = new ArrayList<>(List.of(
                new Order(LocalDateTime.parse("2021-02-09T08:19:22"), "Recovery", 11340),
                new Order(LocalDateTime.parse("2021-02-09T08:42:59"), "Power Engineer", 17480),
                new Order(LocalDateTime.parse("2021-02-09T08:57:51"), "Preparatory", 21410)
        ));

        List<OrderResult> results = service.calculateResults(
                orders,
                0.50,
                0.05,
                10
        );

        assertEquals(new BigDecimal("56700.00"), results.get(0).getFinalPrice());
        assertEquals(new BigDecimal("96140.00"), results.get(1).getFinalPrice());
        assertEquals(new BigDecimal("128460.00"), results.get(2).getFinalPrice());
    }

    @Test
    void testOrdersAreSortedByTime() {

        OrderService service = new OrderService();

        // список в неправильном порядке
        List<Order> orders = new ArrayList<>(List.of(
                new Order(LocalDateTime.parse("2021-02-09T12:00:00"), "CompanyC", 1000),
                new Order(LocalDateTime.parse("2021-02-09T08:00:00"), "CompanyA", 1000),
                new Order(LocalDateTime.parse("2021-02-09T10:00:00"), "CompanyB", 1000)
        ));

        List<OrderResult> results = service.calculateResults(
                orders,
                0.50,
                0.05,
                10
        );

        // Проверка на порядок компаний после сортировки
        assertEquals("CompanyA", results.get(0).getCompany());
        assertEquals("CompanyB", results.get(1).getCompany());
        assertEquals("CompanyC", results.get(2).getCompany());
    }

    @Test
    void testDiscountSequenceAppliedCorrectly() {

        OrderService service = new OrderService();

        // 12 заказов с разными временами, чтобы сортировка не мешала
        List<Order> orders = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            orders.add(new Order(
                    LocalDateTime.parse("2021-02-09T08:00:00").plusMinutes(i),
                    "Company" + i,
                    1000
            ));
        }

        List<OrderResult> results = service.calculateResults(
                orders,
                0.50,
                0.05,
                10
        );

        // Проверка на первые 10 скидок
        assertEquals(new BigDecimal("5000.00"), results.get(0).getFinalPrice()); // 1000 * 10 * 0.50
        assertEquals(new BigDecimal("5500.00"), results.get(1).getFinalPrice()); // 1000 * 10 * 0.55
        assertEquals(new BigDecimal("6000.00"), results.get(2).getFinalPrice()); // 1000 * 10 * 0.60
        assertEquals(new BigDecimal("6500.00"), results.get(3).getFinalPrice());
        assertEquals(new BigDecimal("7000.00"), results.get(4).getFinalPrice());
        assertEquals(new BigDecimal("7500.00"), results.get(5).getFinalPrice());
        assertEquals(new BigDecimal("8000.00"), results.get(6).getFinalPrice());
        assertEquals(new BigDecimal("8500.00"), results.get(7).getFinalPrice());
        assertEquals(new BigDecimal("9000.00"), results.get(8).getFinalPrice());
        assertEquals(new BigDecimal("9500.00"), results.get(9).getFinalPrice());

        // Проверка, что после 10-й скидка = 0.00
        assertEquals(new BigDecimal("10000.00"), results.get(10).getFinalPrice());
        assertEquals(new BigDecimal("10000.00"), results.get(11).getFinalPrice());
    }

    @Test
    void testBigDecimalRoundingIsCorrect() {

        OrderService service = new OrderService();

        // Специально берётся вес, который даст дробный результат
        List<Order> orders = new ArrayList<>(List.of(
                new Order(LocalDateTime.parse("2021-02-09T08:00:00"), "TestCompany", 1234)
        ));

        List<OrderResult> results = service.calculateResults(
                orders,
                0.50,   // скидка 50%
                0.05,
                10
        );

        // Цена без скидки: 1234 * 10 = 12340
        // Скидка 50% → итог: 6170.00
        assertEquals(new BigDecimal("6170.00"), results.get(0).getFinalPrice());
    }
}
