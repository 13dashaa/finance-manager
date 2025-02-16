package com.example.fmanager.models;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Transaction {
    private int id;
    private String description;
    private Float amount;
    private int userId;
    private int categoryId;
    private LocalDateTime date;
    private LocalDateTime createAt;

    public Transaction(int id, String description,
                       Float amount, int userId,
                       int categoryId, LocalDateTime date) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.userId = userId;
        this.categoryId = categoryId;
        this.date = date;
        this.createAt = LocalDateTime.now();
    }
}
