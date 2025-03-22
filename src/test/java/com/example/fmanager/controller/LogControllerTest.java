package com.example.fmanager.controller;

import com.example.fmanager.service.LogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController logController;

    private LocalDate testDate;
    private String logFilePath;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2025, 3, 21); // Новая дата: 21.03.2025
        logFilePath = "logs/logs-2025-03-21.log"; // Новое имя файла
    }

    @Test
    void getLogsByDate_Success() throws IOException {
        when(logService.generateLogFileForDate(testDate.toString())).thenReturn(logFilePath);

        ResponseEntity<Resource> response = logController.getLogsByDate(testDate);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.TEXT_PLAIN, response.getHeaders().getContentType());
        assertEquals("attachment; filename=logs-2025-03-21.log",
                response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
    }

    @Test
    void getLogsByDate_FileNotFound() throws IOException {
        when(logService.generateLogFileForDate(testDate.toString()))
                .thenThrow(new FileNotFoundException("File not found"));

        ResponseEntity<Resource> response = logController.getLogsByDate(testDate);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getLogsByDate_NoLogsForDate() throws IOException {
        when(logService.generateLogFileForDate(testDate.toString()))
                .thenThrow(new NoSuchElementException("No logs found"));

        ResponseEntity<Resource> response = logController.getLogsByDate(testDate);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getLogsByDate_IOException() throws IOException {
        when(logService.generateLogFileForDate(testDate.toString()))
                .thenThrow(new IOException("Server error"));

        ResponseEntity<Resource> response = logController.getLogsByDate(testDate);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}