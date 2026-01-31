package com.company.discountday.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileOrderService {

    public List<String> readFile(String path) throws IOException {
        return Files.readAllLines(Path.of(path));
    }

    public void writeFile(String path, List<String> lines) throws IOException {
        Files.write(Path.of(path), lines);
    }
}
