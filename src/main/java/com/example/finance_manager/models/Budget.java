package com.example.finance_manager.models;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class Budget {
    private int id;
    private int userId;
    private int categoryId;
    private Float limit;
    private int period;
    private LocalDateTime createdAt;

    public Budget(int id, int userId, int categoryId, Float limit, int period) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.limit = limit;
        this.period = period;
        this.createdAt = LocalDateTime.now();
    }


}
