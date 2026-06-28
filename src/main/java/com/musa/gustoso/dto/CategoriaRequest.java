package com.musa.gustoso.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(

    @NotBlank(message = "Cortesemente inserire il Nome della Categoria - Obbligatorio.")
    String nome,
    @NotBlank(message = "Cortesemente inserire il nome della Tipologia - Obbligatorio.")
    String tipo

) {}