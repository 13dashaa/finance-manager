package com.example.fmanager.dto;

import com.example.fmanager.models.Accounts;
import com.example.fmanager.models.Budgets;
import com.example.fmanager.models.Clients;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientDto {

    private int id;
    private String username;
    private String email;
    private Set<Integer> budgetIds;
    private Set<Integer> accountIds;

    public static ClientDto convertToDto(Clients client) {
        ClientDto dto = new ClientDto();
        dto.setId(client.getId());
        Set<Accounts> accounts = client.getAccounts();
        if (accounts != null) {
            dto.setAccountIds(client.getAccounts().stream()
                    .map(Accounts::getId)
                    .collect(Collectors.toSet()));
        } else {
            dto.setAccountIds(new HashSet<>());
        }
        dto.setUsername(client.getUsername());
        dto.setEmail(client.getEmail());
        Set<Budgets> budgets = client.getBudgets();
        if (budgets != null) {
            dto.setBudgetIds(client.getBudgets().stream()
                    .map(Budgets::getId)
                    .collect(Collectors.toSet()));
        } else {
            dto.setBudgetIds(new HashSet<>());
        }
        return dto;
    }
}
