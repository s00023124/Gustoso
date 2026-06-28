package com.musa.gustoso.dto;

import java.time.LocalDateTime;

public record CommentoResponse(
    Long id,
    String autore,
    Long contenutoId,
    String testo,
    LocalDateTime data
) {}
