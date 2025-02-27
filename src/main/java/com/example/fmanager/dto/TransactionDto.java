package com.example.fmanager.dto;

import com.example.fmanager.models.Transactions;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionDto {
    private int id;                // Идентификатор транзакции
    private String description;     // Описание транзакции
    private Float amount;          // Сумма транзакции
    private int accountId;         // Идентификатор аккаунта
    private int categoryId;        // Идентификатор категории
    private LocalDateTime date;    // Дата транзакции
    private LocalDateTime createdAt; // Временная метка создания


    public static TransactionDto convertToDto(Transactions transaction) {
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
