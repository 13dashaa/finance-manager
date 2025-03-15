package com.example.fmanager.dto;

import java.util.Set;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BudgetCreateDto {
    @NotNull(message = "Period cannot be null")
    private int period;
    @NotNull(message = "Limitation cannot be null")
    @Positive(message = "Limitation cannot be negative")
    private Float limitation;
    @Min(value = 1, message = "Only one category ID must be provided")
    private int categoryId;
    @Size(min = 1, message = "At least one client ID must be provided")
    private Set<Integer> clientIds;
}
