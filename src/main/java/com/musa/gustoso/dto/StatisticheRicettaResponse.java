package com.musa.gustoso.dto;

public record StatisticheRicettaResponse(
    Long ricettaId,
    String titolo,
    Double mediaVoti,       
    long numeroRecensioni
) {}
