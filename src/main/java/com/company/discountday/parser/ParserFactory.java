package com.company.discountday.parser;

public class ParserFactory {

    public static OrderParser createParser(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("Путь к файлу не может быть пустым");
        }

        if (filePath.endsWith(".txt")) {

            return new PipeDelimitedOrderParser();
        } else if (!filePath.contains(".") || filePath.lastIndexOf('.') < filePath.lastIndexOf('/')) {

            return new HashDelimitedOrderParser();
        } else {
            throw new IllegalArgumentException(
                    "Неподдерживаемый формат файла: " + filePath +
                            ". Поддерживаются только .txt и файлы без расширения"
            );
        }
    }
}