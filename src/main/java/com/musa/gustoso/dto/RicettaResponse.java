package com.musa.gustoso.dto;

import java.time.LocalDate;
import java.util.List;

public record RicettaResponse(
    Long id,
    String titolo,
    String copertina,
    Integer tempoPreparazione,
    String difficolta,
    String procedimento,
    String autore,
    List<String> categorie,
    LocalDate dataPubblicazione,
    List<RicettaIngredienteInfo> ingredienti
) {}
