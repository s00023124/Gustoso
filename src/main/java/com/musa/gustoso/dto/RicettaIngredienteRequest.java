package com.musa.gustoso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RicettaIngredienteRequest(
    @NotNull(message = "Id della ricetta - Obbligatorio.")
    @Positive(message = "Inserire un valore positivo")
    Long ricettaId,

    @NotNull(message = "Id dell'ingrediente - Obbligatorio.")
    @Positive(message = "Inserire un valore positivo")
    Long ingredienteId,

    @NotNull(message = "Quantita - Obbligatoria.")
    @Positive(message = "La quantita deve essere positiva")
    Double quantita,

    @NotBlank(message = "Unita di misura - Obbligatoria.")
    String unita
) {}
