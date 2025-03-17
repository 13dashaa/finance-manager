package com.example.fmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientUpdateDto {
    @NotBlank(message = "Username cannot be null")
    String username;
}
