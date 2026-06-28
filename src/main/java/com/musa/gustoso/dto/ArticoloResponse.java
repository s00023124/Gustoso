package com.musa.gustoso.dto;

import java.time.LocalDate;

public record ArticoloResponse(
    Long id,
    String titolo,
    String copertina,
    Integer tempoDiLettura,
    String corpo,
    String autore,                  
    LocalDate dataPubblicazione
) {}
