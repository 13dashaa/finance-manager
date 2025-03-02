package com.example.fmanager.dto;

import com.example.fmanager.models.Goals;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GoalDto {
    private int id;
    private String name;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount = BigDecimal.ZERO;
    private LocalDate startDate;
    private LocalDate endDate;
    private int clientId;

    public static GoalDto convertToDto(Goals goal) {
        GoalDto dto = new GoalDto();
        dto.setId(goal.getId());
        dto.setClientId(goal.getClient().getId());
        dto.setName(goal.getName());
        dto.setTargetAmount(goal.getTargetAmount());
        dto.setCurrentAmount(goal.getCurrentAmount());
        dto.setStartDate(goal.getStartDate());
        dto.setEndDate(goal.getEndDate());
        return dto;
    }
}
