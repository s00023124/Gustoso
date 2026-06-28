package com.musa.gustoso.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UserRequest(
    @NotBlank(message = "Cortesemente inserire il Username - Obbligatorio.")
    String username,
    @NotBlank(message = "Cortesemente inserire Email - Obbligatoria.")
    @Email(message = "Inserire un'email valida")
    String email,
    @NotBlank(message = "Cortesemente inserire Telefono - Obbligatorio.")
    @Pattern(regexp = "^[0-9]{10}$", message = "Inserire un numero di 10 cifre senza spazi né prefisso (es. 1231234567)")
    String telefono,
    @NotBlank(message = "Cortesemente inserire Password - Obbligatorio.")
    String password,
    String immagineProfilo

)    
{}
