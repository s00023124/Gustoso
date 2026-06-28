package com.musa.gustoso.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RecensioneRequest(
    @NotNull(message = "Id dell'autore - Obbligatorio.")
    @Positive(message = "Inserire un valore positivo")
    Long autoreId,

    @NotNull(message = "Id della ricetta - Obbligatorio.")
    @Positive(message = "Inserire un valore positivo")
    Long ricettaId,

    @NotNull(message = "Il voto - Obbligatorio.")
    @Min(value = 1, message = "Il voto minimo e 1")
    @Max(value = 5, message = "Il voto massimo e 5")
    Integer voto,

    String testo
) {}
