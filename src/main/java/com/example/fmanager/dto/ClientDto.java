package com.example.fmanager.dto;

import com.example.fmanager.models.Account;
import com.example.fmanager.models.Budget;
import com.example.fmanager.models.Client;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDto {

    private int id;                // Идентификатор клиента
    private String username;       // Имя пользователя
    private String email;          // Электронная почта
    private Set<Integer> budgetIds;
    private Set<Integer> accountIds;


    public static ClientDto convertToDto(Client client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        Set<Account> accounts = client.getAccounts();
        if (accounts != null) {
            dto.setAccountIds(client.getAccounts().stream()
                    .map(Account::getId)
                    .collect(Collectors.toSet()));
        } else {
            dto.setAccountIds(new HashSet<>());
        }
        dto.setUsername(client.getUsername());
        dto.setEmail(client.getEmail());
        Set<Budget> budgets = client.getBudgets();
        if (budgets != null) {
            dto.setBudgetIds(client.getBudgets().stream()
                    .map(Budget::getId)
                    .collect(Collectors.toSet()));
        } else {
            dto.setBudgetIds(new HashSet<>());
        }
        return dto;
    }
}
