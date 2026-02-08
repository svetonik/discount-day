package com.company.discountday.manager;

import com.company.discountday.model.Order;
import com.company.discountday.model.OrderResult;
import com.company.discountday.service.FileOrderService;
import com.company.discountday.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


// Unit-тесты с использованием Mockito

@ExtendWith(MockitoExtension.class)
class OrderManagerTest {

    @Mock
    private FileOrderService fileOrderService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderManager orderManager;

    private List<String> sampleLines;
    private List<Order> sampleOrders;
    private List<OrderResult> sampleResults;

    @BeforeEach
    void setUp() {
        // Подготовка тестовых данных
        sampleLines = Arrays.asList(
                "2021-02-09T08:19:22|Recovery|11340",
                "2021-02-09T08:42:59|Power Engineer|17480"
        );

        sampleOrders = Arrays.asList(
                new Order(LocalDateTime.parse("2021-02-09T08:19:22"), "Recovery", 11340),
                new Order(LocalDateTime.parse("2021-02-09T08:42:59"), "Power Engineer", 17480)
        );

        sampleResults = Arrays.asList(
                new OrderResult("Recovery", new BigDecimal("56700.00")),
                new OrderResult("Power Engineer", new BigDecimal("96140.00"))
        );
    }

    //Тест успешной обработки заказов с файлом .txt (разделитель "|")
    @Test
    void testProcessOrders_WithTxtFile_Success() throws IOException {
        // Arrange (Подготовка)
        String inputPath = "src/main/resources/orders.txt";
        String outputPath = "src/main/resources/result.txt";

        // Настраиваем поведение моков
        when(fileOrderService.readFile(inputPath)).thenReturn(sampleLines);
        when(orderService.calculateResults(anyList(), eq(0.50), eq(0.05), eq(10.0)))
                .thenReturn(sampleResults);

        // Act (Действие)
        orderManager.processOrders(inputPath, outputPath, 0.50, 0.05, 10);

        // Assert (Проверка)
        // Проверяем, что readFile был вызван с правильным путём
        verify(fileOrderService).readFile(inputPath);

        // Проверяем, что calculateResults был вызван с правильными параметрами
        verify(orderService).calculateResults(
                argThat(orders -> orders.size() == 2), // проверяем размер списка
                eq(0.50),
                eq(0.05),
                eq(10.0)
        );

        // Проверяем, что writeFile был вызван
        verify(fileOrderService).writeFile(
                eq(outputPath),
                argThat(lines -> lines.size() == 2 &&
                        lines.get(0).equals("Recovery - 56700.00") &&
                        lines.get(1).equals("Power Engineer - 96140.00"))
        );
    }

    // Тест успешной обработки заказов с файлом без расширения (разделитель "#")
    @Test
    void testProcessOrders_WithoutExtension_Success() throws IOException {
        // Arrange
        String inputPath = "src/main/resources/orders";
        String outputPath = "src/main/resources/result2.txt";

        List<String> hashDelimitedLines = Arrays.asList(
                "2021-02-09T08:19:22#Recovery#11340",
                "2021-02-09T08:42:59#Power Engineer#17480"
        );

        when(fileOrderService.readFile(inputPath)).thenReturn(hashDelimitedLines);
        when(orderService.calculateResults(anyList(), eq(0.50), eq(0.05), eq(10.0)))
                .thenReturn(sampleResults);

        // Act
        orderManager.processOrders(inputPath, outputPath, 0.50, 0.05, 10);

        // Assert
        verify(fileOrderService).readFile(inputPath);
        verify(orderService).calculateResults(anyList(), eq(0.50), eq(0.05), eq(10.0));
        verify(fileOrderService).writeFile(eq(outputPath), anyList());
    }

    //Тест с пустым списком заказов
    @Test
    void testProcessOrders_WithEmptyFile_Success() throws IOException {
        // Arrange
        String inputPath = "src/main/resources/empty.txt";
        String outputPath = "src/main/resources/result_empty.txt";

        when(fileOrderService.readFile(inputPath)).thenReturn(Arrays.asList());
        when(orderService.calculateResults(anyList(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Arrays.asList());

        // Act
        orderManager.processOrders(inputPath, outputPath, 0.50, 0.05, 10);

        // Assert
        verify(fileOrderService).readFile(inputPath);
        verify(orderService).calculateResults(
                argThat(List::isEmpty), // проверяем что список пустой
                eq(0.50),
                eq(0.05),
                eq(10.0)
        );
        verify(fileOrderService).writeFile(eq(outputPath), argThat(List::isEmpty));
    }

    //Тест обработки исключения при чтении файла
    @Test
    void testProcessOrders_ReadFileThrowsException() throws IOException {
        // Arrange
        String inputPath = "src/main/resources/nonexistent.txt";
        String outputPath = "src/main/resources/result.txt";

        // Настраиваем мок, чтобы он выбросил исключение
        when(fileOrderService.readFile(inputPath))
                .thenThrow(new IOException("File not found"));

        // Act & Assert
        try {
            orderManager.processOrders(inputPath, outputPath, 0.50, 0.05, 10);
        } catch (IOException e) {
            // Ожидаем, что исключение будет проброшено
        }

        // Проверяем, что readFile был вызван
        verify(fileOrderService).readFile(inputPath);

        // Проверяем, что orderService НЕ был вызван (т.к. упали на чтении)
        verify(orderService, never()).calculateResults(anyList(), anyDouble(), anyDouble(), anyDouble());

        // Проверяем, что writeFile НЕ был вызван
        verify(fileOrderService, never()).writeFile(anyString(), anyList());
    }

    //Тест с различными параметрами скидок
    @Test
    void testProcessOrders_WithDifferentDiscountParameters() throws IOException {
        // Arrange
        String inputPath = "src/main/resources/orders.txt";
        String outputPath = "src/main/resources/result.txt";
        double startDiscount = 0.60;
        double discountStep = 0.10;
        double pricePerKg = 15.0;

        when(fileOrderService.readFile(inputPath)).thenReturn(sampleLines);
        when(orderService.calculateResults(anyList(), eq(startDiscount), eq(discountStep), eq(pricePerKg)))
                .thenReturn(sampleResults);

        // Act
        orderManager.processOrders(inputPath, outputPath, startDiscount, discountStep, pricePerKg);

        // Assert
        verify(orderService).calculateResults(
                anyList(),
                eq(0.60),  // проверяем конкретные значения
                eq(0.10),
                eq(15.0)
        );
    }


     //Тест проверки количества вызовов методов
    @Test
    void testProcessOrders_VerifyMethodCallCounts() throws IOException {
        // Arrange
        String inputPath = "src/main/resources/orders.txt";
        String outputPath = "src/main/resources/result.txt";

        when(fileOrderService.readFile(inputPath)).thenReturn(sampleLines);
        when(orderService.calculateResults(anyList(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(sampleResults);

        // Act
        orderManager.processOrders(inputPath, outputPath, 0.50, 0.05, 10);

        // Assert - проверяем, что каждый метод вызван ровно 1 раз
        verify(fileOrderService, times(1)).readFile(inputPath);
        verify(orderService, times(1)).calculateResults(anyList(), anyDouble(), anyDouble(), anyDouble());
        verify(fileOrderService, times(1)).writeFile(eq(outputPath), anyList());

        // Проверяем, что больше никаких взаимодействий не было
        verifyNoMoreInteractions(fileOrderService);
        verifyNoMoreInteractions(orderService);
    }
}