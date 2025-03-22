package com.example.fmanager.service;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class LogServiceTest {

    private LogService logService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        logService = new LogService();
    }

    @Test
    void generateLogFileForDateAsync_FileNotFound_ShouldThrowException() {
        CompletableFuture<String> future = logService.generateLogFileForDateAsync("2025-03-15");
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof RuntimeException);
    }

    @Test
    void generateLogFileForDateAsync_NoLogsForDate_ShouldThrowException() throws IOException {
        Path logFile = tempDir.resolve("application.log");
        Files.write(logFile, "Other log entry".getBytes());

        try (MockedStatic<Paths> pathsMockedStatic = Mockito.mockStatic(Paths.class)) {
            pathsMockedStatic.when(() -> Paths.get("logs/application.log")).thenReturn(logFile);

            CompletableFuture<String> future = logService.generateLogFileForDateAsync("2025-03-15");

            ExecutionException exception = assertThrows(ExecutionException.class, future::get);
            assertTrue(exception.getCause() instanceof NoSuchElementException);
        }
    }

    @Test
    void generateLogFileForDateAsync_ValidLogs_ShouldGenerateFile()
            throws IOException, ExecutionException, InterruptedException {
        Path logFile = tempDir.resolve("application.log");
        String logEntry = "2025-03-22 12:00:00 INFO Some log message";
        Files.write(logFile, logEntry.getBytes());
        Path logsDir = tempDir.resolve("logs");
        Files.createDirectories(logsDir);

        try (MockedStatic<Paths> pathsMockedStatic = Mockito.mockStatic(Paths.class)) {
            pathsMockedStatic.when(() -> Paths.get("logs/application.log")).thenReturn(logFile);
            pathsMockedStatic.when(() -> Paths.get("logs/")).thenReturn(logsDir);

            CompletableFuture<String> future = logService.generateLogFileForDateAsync("2025-03-22");
            String logId = future.get();
            assertNotNull(logId);
            assertTrue(logService.getLogFilePath(logId).contains("logs-2025-03-22.log"));
            assertTrue(logService.isTaskCompleted(logId));
        }
    }

    @Test
    void getLogFilePath_NonExistingLogId_ShouldReturnNull() {
        assertNull(logService.getLogFilePath(UUID.randomUUID().toString()));
    }

    @Test
    void isTaskCompleted_NonExistingLogId_ShouldReturnFalse() {
        assertFalse(logService.isTaskCompleted(UUID.randomUUID().toString()));
    }
}