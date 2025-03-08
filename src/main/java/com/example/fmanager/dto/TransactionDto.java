package com.example.fmanager.dto;

import com.example.fmanager.models.Transaction;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDto {
    private int id;
    private String description;
    private Float amount;
    private int accountId;
    private int categoryId;
    private LocalDateTime date;
    private LocalDateTime createdAt;

    public static TransactionDto convertToDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setCategoryId(transaction.getCategory().getId());
        dto.setAccountId(transaction.getAccount().getId());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setDate(transaction.getDate());
        dto.setCreatedAt(transaction.getCreatedAt());
        return dto;
    }
}
