package com.example.fmanager.dto;

import com.example.fmanager.models.Account;
import com.example.fmanager.models.Budget;
import com.example.fmanager.models.Client;
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
        dto.setAccountIds(client.getAccounts().stream()
                .map(Account::getId)
                .collect(Collectors.toSet()));
        dto.setUsername(client.getUsername());
        dto.setEmail(client.getEmail());
        dto.setBudgetIds(client.getBudgets().stream()
                .map(Budget::getId)
                .collect(Collectors.toSet()));
        return dto;
    }
}
