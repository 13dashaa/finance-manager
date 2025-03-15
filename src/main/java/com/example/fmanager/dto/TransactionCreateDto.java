package com.example.fmanager.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PastOrPresent;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionCreateDto {

    private String description;
    @NotNull(message = "Amount can not be null")
    private Float amount;
    @PastOrPresent(message = "The transaction date must be in past or present")
    private LocalDateTime date;
    @Min(value = 1,  message = "Only one category ID must be provided")
    private int categoryId;
    @Min(value = 1, message = "Only one account ID must be provided")
    private int accountId;
}
