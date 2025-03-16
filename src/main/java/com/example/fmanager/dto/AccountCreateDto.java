package com.example.fmanager.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountCreateDto {
    @NotBlank(message = "Name cannot be null")
    private String name;
    @NotNull(message = "Balance cannot be null")
    @Min(value = 1, message = "Balance must be greater than zero")
    private Float balance;
    @Min(1)
    private int clientId;
}
