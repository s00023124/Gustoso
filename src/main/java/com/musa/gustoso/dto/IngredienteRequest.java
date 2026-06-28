package com.musa.gustoso.dto;

import jakarta.validation.constraints.NotBlank;

public record IngredienteRequest(
    @NotBlank(message = "Cortesemente inserire il Nome dell'ingrediente - Obbligatorio.")
    String nome
) {}
