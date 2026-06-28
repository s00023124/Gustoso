package com.musa.gustoso.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Username obbligatorio")
    String username,
    @NotBlank(message = "Password obbligatoria")
    String password
) {}