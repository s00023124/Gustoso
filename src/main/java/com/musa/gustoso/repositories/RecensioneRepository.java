package com.musa.gustoso.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.musa.gustoso.entities.Recensione;

public interface RecensioneRepository extends JpaRepository<Recensione, Long> {

    @Query("SELECT AVG(r.voto) FROM Recensione r WHERE r.ricetta.id = :ricettaId")
    Double mediaVotiByRicettaId(@Param("ricettaId") Long ricettaId);

    long countByRicettaId(Long ricettaId);
}
