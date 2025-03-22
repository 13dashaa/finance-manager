package com.example.fmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ClientUpdateDto {
    @NotBlank(message = "Username cannot be null")
    String username;
}
