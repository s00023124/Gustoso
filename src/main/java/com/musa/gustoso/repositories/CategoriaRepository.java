package com.musa.gustoso.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musa.gustoso.entities.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Optional<Categoria> findByNome(String nome);
}
