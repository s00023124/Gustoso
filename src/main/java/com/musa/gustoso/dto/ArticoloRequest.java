package com.musa.gustoso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ArticoloRequest(
    @NotBlank(message = "Cortesemente inserire il Titolo - Obbligatorio.")
    String titolo,

    @NotBlank(message = "Cortesemente inserire l'Immagine per la copertina - Obbligatorio.")
    String copertina,

    @NotNull(message = "Cortesemente inserire il tempo di lettura - Obbligatorio.")
    @Positive(message = "Cortesemente inserire un valore Positivo")
    Integer tempoDiLettura,

    @NotBlank(message = "Cortesemente inserire il Corpo dell'articolo - Obbligatorio.")
    String corpo,

    @NotNull(message = "Cortesemente inserire Id dell'autore - Obbligatorio.")
    @Positive(message = "Cortesemente inserire un valore Positivo")
    Long autoreId
) {}
