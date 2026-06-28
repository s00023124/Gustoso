package com.musa.gustoso.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String username,
    String email,
    String telefono,
    String immagineProfilo,
    LocalDateTime dataIscrizione,
    String ruolo

) {}

