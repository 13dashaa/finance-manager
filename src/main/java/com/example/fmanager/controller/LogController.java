package com.example.fmanager.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/logs")
public class LogController {

    private static final String LOG_DIRECTORY = "logs"; // Папка с логами
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping()
    public ResponseEntity<Resource> getLogsByDate(@RequestParam String date) throws IOException {
        // Преобразуем строку даты в LocalDate
        LocalDate logDate = LocalDate.parse(date, DATE_FORMATTER);

        // Формируем имя файла логов для указанной даты
        String logFileName = "application-" + logDate.format(DATE_FORMATTER) + ".log";
        Path logFilePath = Paths.get(LOG_DIRECTORY, logFileName);

        // Проверяем, существует ли файл
        if (!Files.exists(logFilePath)) {
            return ResponseEntity.notFound().build();
        }

        // Читаем файл и возвращаем его содержимое
        Resource logFileResource = new FileSystemResource(logFilePath.toFile());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + logFileName + "\"")
                .contentType(MediaType.TEXT_PLAIN)
                .body(logFileResource);
    }
}