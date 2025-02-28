package com.example.fmanager.dto;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import com.example.fmanager.models.Budgets;
import com.example.fmanager.models.Clients;
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


    public static BudgetDto convertToDto(Budgets budget) {
        BudgetDto dto = new BudgetDto();
        dto.setId(budget.getId());
        Set<Clients> clients = budget.getClients();
        if (clients != null) {
            dto.setClientIds(budget.getClients().stream()
                    .map(Clients::getId)
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

