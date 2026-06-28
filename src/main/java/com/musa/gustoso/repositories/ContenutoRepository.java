package com.musa.gustoso.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.musa.gustoso.entities.Contenuto;

public interface ContenutoRepository extends JpaRepository<Contenuto, Long> {

    @Query("SELECT c FROM Contenuto c ORDER BY c.dataPubblicazione DESC")
    List<Contenuto> findAllOrdinatiPerData();

    @Query("SELECT c FROM Contenuto c WHERE TYPE(c) = com.musa.gustoso.entities.Ricetta")
    List<Contenuto> findSoloRicette();

    @Query("SELECT c FROM Contenuto c WHERE TYPE(c) = com.musa.gustoso.entities.Articolo")
    List<Contenuto> findSoloArticoli();

    @Query("SELECT c FROM Contenuto c WHERE LOWER(c.titolo) LIKE LOWER(CONCAT('%', :parola, '%'))")
    List<Contenuto> cercaPerTitolo(@Param("parola") String parola);

    @Query("SELECT c FROM Contenuto c WHERE c.autore.username = :username ORDER BY c.dataPubblicazione DESC")
    List<Contenuto> findByAutoreUsername(@Param("username") String username);
}
