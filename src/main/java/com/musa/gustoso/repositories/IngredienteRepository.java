package com.musa.gustoso.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musa.gustoso.entities.Ingrediente;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    Optional<Ingrediente> findByNome(String nome);
}
