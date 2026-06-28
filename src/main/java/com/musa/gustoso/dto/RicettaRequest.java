package com.musa.gustoso.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record RicettaRequest(
    @NotBlank(message = "Cortesemente inserire il Titolo - Obbligatorio.")
    String titolo,

    @NotBlank(message = "Cortesemente inserire l'Immagine per la copertina - Obbligatorio.")
    String copertina,

    @NotNull(message = "Cortesemente inserire un tempo di preparazione - Obbligatorio.")
    @Positive(message = "Cortesemente inserire un valore Positivo")
    Integer tempoPreparazione,

    @NotBlank(message = "Cortesemente inserire la Difficoltà (Bassa|Media|Alta) - Obbligatorio.")
    String difficolta,

    @NotBlank(message = "Cortesemente inserire il Procedimento per la Ricetta - Obbligatorio.")
    String procedimento,

    @NotNull(message = "Cortesemente inserire Id dell'autore - Obbligatorio.")
    @Positive(message = "Cortesemente inserire un valore Positivo")
    Long autoreId,

    List<Long> categorieIds
) {}
