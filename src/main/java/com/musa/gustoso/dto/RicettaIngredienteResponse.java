package com.musa.gustoso.dto;

public record RicettaIngredienteResponse(
    Long id,
    String ricetta,
    String ingrediente,
    Double quantita,
    String unita
) {}
