package com.musa.gustoso.dto;

import java.time.LocalDateTime;

public record RecensioneResponse(
    Long id,
    String autore,
    String ricetta,
    Integer voto,
    String testo,
    LocalDateTime data
) {}
