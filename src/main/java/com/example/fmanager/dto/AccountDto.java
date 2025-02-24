package com.example.fmanager.dto;

import com.example.fmanager.models.Account;
import com.example.fmanager.models.Transaction;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountDto {

    private int id;
    private String name;
    private Float balance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int clientId; // или ClientDTO для полной информации о клиенте
    private Set<Integer> transactionIds;


    public static AccountDto convertToDto(Account account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        dto.setTransactionIds(account.getTransactions().stream()
                .map(Transaction::getId)
                .collect(Collectors.toSet()));
        dto.setClientId(account.getClient().getId());
        dto.setName(account.getName());
        dto.setBalance(account.getBalance());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        return dto;
    }
}