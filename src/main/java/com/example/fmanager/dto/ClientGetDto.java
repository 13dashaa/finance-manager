package com.example.fmanager.dto;

import com.example.fmanager.models.Account;
import com.example.fmanager.models.Budget;
import com.example.fmanager.models.Client;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientGetDto {

    private int id;
    private String username;
    private String email;
    private Set<String> budgetCategoryNames;
    private Set<String> accountNames;

    public static ClientGetDto convertToDto(Client client) {
        ClientGetDto dto = new ClientGetDto();
        dto.setId(client.getId());
        Set<Account> accounts = client.getAccounts();
        if (accounts != null) {
            dto.setAccountNames(accounts.stream()
                    .map(Account::getName) // Предполагается, что у Account есть метод getName()
                    .collect(Collectors.toSet()));
        } else {
            dto.setAccountNames(new HashSet<>());
        }

        // Получаем имена бюджетов
        Set<Budget> budgets = client.getBudgets();
        if (budgets != null) {
            dto.setBudgetCategoryNames(budgets.stream()
                    .map(budget -> budget.getCategory().getName()) // Предполагается, что у Budget есть метод getName()
                    .collect(Collectors.toSet()));
        } else {
            dto.setBudgetCategoryNames(new HashSet<>());
        }
        dto.setUsername(client.getUsername());
        dto.setEmail(client.getEmail());

        return dto;
    }
}
