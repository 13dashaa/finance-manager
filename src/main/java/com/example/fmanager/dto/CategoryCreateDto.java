package com.example.fmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CategoryCreateDto {
    @NotBlank(message = "Name cannot be null")
    String name;
}
