package com.example.fmanager.dto;

import com.example.fmanager.models.Budget;
import com.example.fmanager.models.Client;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BudgetDto {

    private int id;
    private Set<Integer> clientIds;
    private Integer categoryId;
    private Float limitation;
    private int period;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BudgetDto convertToDto(Budget budget) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        Set<Client> clients = budget.getClients();
        if (clients != null) {
            dto.setClientIds(budget.getClients().stream()
                    .map(Client::getId)
                    .collect(Collectors.toSet()));
        } else {
            dto.setClientIds(new HashSet<>());
        }
        dto.setCategoryId(budget.getCategory().getId());
        dto.setLimitation(budget.getLimitation());
        dto.setPeriod(budget.getPeriod());
        dto.setCreatedAt(budget.getCreatedAt());
        dto.setUpdatedAt(budget.getUpdatedAt());
        return dto;
    }
}

