package com.musa.gustoso.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musa.gustoso.entities.Ricetta;

public interface RicettaRepository extends JpaRepository<Ricetta, Long> {

      List<Ricetta> findByTitoloContainingIgnoreCase(String titolo);

    List<Ricetta> findByDifficolta(String difficolta);

    List<Ricetta> findByAutoreUsername(String username);

    List<Ricetta> findByCategorieId(Long categoriaId);

    List<Ricetta> findDistinctByCategorieNome(String nome);

    List<Ricetta> findDistinctByRicettaIngredientiIngredienteNome(String nome);

    List<Ricetta> findAllByOrderByDataPubblicazioneDesc();


    List<Ricetta> findAllByOrderByTempoPreparazioneAsc();
}
