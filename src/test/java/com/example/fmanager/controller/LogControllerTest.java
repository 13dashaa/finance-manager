package com.example.fmanager.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.fmanager.service.LogService;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController logController;

    private LocalDate testDate;
    private String taskId;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2023, 3, 24);
        taskId = "12345";
    }

    @Test
    void generateLogsByDate_Success(){
        CompletableFuture<String> future = CompletableFuture.completedFuture(taskId);
        when(logService.generateLogFileForDateAsync(anyString())).thenReturn(future);

        ResponseEntity<String> response = logController.generateLogsByDate(testDate);

        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        assertEquals("Task started. ID: " + taskId, response.getBody());
    }

    @Test
    void generateLogsByDate_Interrupted(){
        CompletableFuture<String> future = new CompletableFuture<>();
        when(logService.generateLogFileForDateAsync(anyString())).thenReturn(future);

        Thread.currentThread().interrupt();
        ResponseEntity<String> response = logController.generateLogsByDate(testDate);

        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
        assertTrue(response.getBody().contains("Task had been interrupted"));
    }

    @Test
    void generateLogsByDate_ExecutionException(){
        CompletableFuture<String> future = CompletableFuture
                .failedFuture(
                        new ExecutionException("Execution error", new Throwable()
                        )
                );
        when(logService.generateLogFileForDateAsync(anyString())).thenReturn(future);

        ResponseEntity<String> response = logController.generateLogsByDate(testDate);

       //assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void getTaskStatus_Completed() {
        when(logService.isTaskCompleted(anyString())).thenReturn(true);

        ResponseEntity<String> response = logController.getTaskStatus(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Task completed", response.getBody());
    }

    @Test
    void getTaskStatus_NotFound() {
        when(logService.isTaskCompleted(anyString())).thenReturn(false);

        ResponseEntity<String> response = logController.getTaskStatus(taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Task not found or not completed", response.getBody());
    }

    @Test
    void getLogFileById_FileFound(){
        String logFilePath = "logs/logs-2023-03-24.log";
        when(logService.getLogFilePath(anyString())).thenReturn(logFilePath);

        ResponseEntity<Resource> response = logController.getLogFileById(taskId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("logs-2023-03-24.log", response.getHeaders().getContentDisposition().getFilename());
    }

    @Test
    void getLogFileById_FileNotFound() {
        when(logService.getLogFilePath(anyString())).thenReturn(null);

        ResponseEntity<Resource> response = logController.getLogFileById(taskId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

}