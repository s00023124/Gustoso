package com.musa.gustoso.dto;

public record LoginResponse(
    String token,
    String username,
    String ruolo
) {}