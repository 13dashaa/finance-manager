package com.example.fmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @InjectMocks
    private LogService logService;

    private final String testDate = "21.03.2025";
    private final String logFileName = "logs/logs-21.03.2025.log";

    @BeforeEach
    void setUp() throws IOException {

        Path logsDir = Paths.get("logs/");
        if (!Files.exists(logsDir)) {
            Files.createDirectories(logsDir);
        }
    }

    @Test
    void generateLogFileForDate_Success() throws IOException {

        Path logPath = Paths.get("logs/application.log");
        List<String> filteredLines = List.of(
                "21.03.2025 10:00:00 - INFO: Application started",
                "21.03.2025 10:05:00 - INFO: User logged in"
        );

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(logPath)).thenReturn(true);
            filesMock.when(() -> Files.lines(logPath)).thenReturn(filteredLines.stream());
            filesMock.when(() -> Files.write(any(Path.class), anyList())).thenReturn(Paths.get(logFileName));

            String result = logService.generateLogFileForDate(testDate);

            assertEquals(logFileName, result);
            filesMock.verify(() -> Files.write(Paths.get(logFileName), filteredLines), times(1));
        }
    }

    @Test
    void generateLogFileForDate_FileNotFound() {

        Path logPath = Paths.get("logs/application.log");

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(logPath)).thenReturn(false);

            assertThrows(FileNotFoundException.class, () -> logService.generateLogFileForDate(testDate));
        }
    }

    @Test
    void generateLogFileForDate_NoLogsForDate(){
        Path logPath = Paths.get("logs/application.log");
        List<String> filteredLines = List.of();

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(logPath)).thenReturn(true);
            filesMock.when(() -> Files.lines(logPath)).thenReturn(filteredLines.stream());

            assertThrows(NoSuchElementException.class, () -> logService.generateLogFileForDate(testDate));
        }
    }

    @Test
    void generateLogFileForDate_IOException(){

        Path logPath = Paths.get("logs/application.log");
        List<String> filteredLines = List.of(
                "21.03.2025 10:00:00 - INFO: Application started"
        );

        try (MockedStatic<Files> filesMock = Mockito.mockStatic(Files.class)) {
            filesMock.when(() -> Files.exists(logPath)).thenReturn(true);
            filesMock.when(() -> Files.lines(logPath)).thenReturn(filteredLines.stream());
            filesMock.when(() -> Files.write(any(Path.class), anyList())).thenThrow(new IOException("Failed to write file"));

            assertThrows(IOException.class, () -> logService.generateLogFileForDate(testDate));
        }
    }
}