package com.example.fmanager.dto;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ErrorResponse {
    private LocalDateTime timestamp; // Время возникновения ошибки
    private int status;             // HTTP-статус код
    private String error;           // Тип ошибки (например, "Bad Request")
    private String message;         // Сообщение об ошибке
    private String path;            // Путь, по которому произошла ошибка

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}