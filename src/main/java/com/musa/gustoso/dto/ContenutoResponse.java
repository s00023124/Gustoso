package com.musa.gustoso.dto;

import java.time.LocalDate;

public record ContenutoResponse(
    Long id,
    String tipo,           
    String titolo,
    LocalDate dataPubblicazione,
    String copertina,
    String autore
) {}
