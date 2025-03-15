package com.example.fmanager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientCreateDto {
    @NotBlank(message = "Username cannot be null")
    String username;
    @Email(message = "Email must be valid format")
    String email;
    @Size(min = 8, max = 64, message = "Password must be between 8 and 64 characters")
    String password;
}
