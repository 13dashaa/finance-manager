package com.example.fmanager.dto;

import com.example.fmanager.models.Accounts;
import com.example.fmanager.models.Transactions;
import java.time.LocalDateTime;
import java.util.HashSet;
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
    private int clientId;
    private Set<Integer> transactionIds;

    public static AccountDto convertToDto(Accounts account) {
        AccountDto dto = new AccountDto();
        dto.setId(account.getId());
        Set<Transactions> transactions = account.getTransactions();
        if (transactions != null) {
            dto.setTransactionIds(account.getTransactions().stream()
                .map(Transactions::getId)
                .collect(Collectors.toSet()));
        } else {
            dto.setTransactionIds(new HashSet<>());
        }
        dto.setClientId(account.getClient().getId());
        dto.setName(account.getName());
        dto.setBalance(account.getBalance());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        return dto;
    }
}