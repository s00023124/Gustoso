package com.musa.gustoso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CommentoRequest(
    @NotNull(message = "Id dell'autore - Obbligatorio.")
    @Positive(message = "Inserire un valore positivo")
    Long autoreId,

    @NotNull(message = "Id del contenuto - Obbligatorio.")
    @Positive(message = "Inserire un valore positivo")
    Long contenutoId,

    @NotBlank(message = "Il testo del commento - Obbligatorio.")
    String testo
) {}
