package com.musa.gustoso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musa.gustoso.entities.Commento;

public interface CommentoRepository extends JpaRepository<Commento, Long> {
}
