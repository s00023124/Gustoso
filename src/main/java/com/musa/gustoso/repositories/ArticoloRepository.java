package com.musa.gustoso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musa.gustoso.entities.Articolo;

public interface ArticoloRepository extends JpaRepository<Articolo, Long> {
}
