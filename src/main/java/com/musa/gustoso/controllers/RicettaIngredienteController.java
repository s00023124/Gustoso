package com.musa.gustoso.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musa.gustoso.dto.RicettaIngredienteRequest;
import com.musa.gustoso.dto.RicettaIngredienteResponse;
import com.musa.gustoso.entities.Ingrediente;
import com.musa.gustoso.entities.Ricetta;
import com.musa.gustoso.entities.RicettaIngrediente;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.IngredienteRepository;
import com.musa.gustoso.repositories.RicettaIngredienteRepository;
import com.musa.gustoso.repositories.RicettaRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/ricetta-ingredienti")
public class RicettaIngredienteController {

    private final RicettaIngredienteRepository ricettaIngredienteRepository;
    private final RicettaRepository ricettaRepository;
    private final IngredienteRepository ingredienteRepository;

    public RicettaIngredienteController(RicettaIngredienteRepository ricettaIngredienteRepository,
            RicettaRepository ricettaRepository, IngredienteRepository ingredienteRepository) {
        this.ricettaIngredienteRepository = ricettaIngredienteRepository;
        this.ricettaRepository = ricettaRepository;
        this.ingredienteRepository = ingredienteRepository;
    }

    private RicettaIngredienteResponse toResponse(RicettaIngrediente ri) {
        return new RicettaIngredienteResponse(
            ri.getId(),
            ri.getRicetta().getTitolo(),
            ri.getIngrediente().getNome(),
            ri.getQuantita(),
            ri.getUnita()
        );
    }

    @GetMapping
    public List<RicettaIngredienteResponse> getAll() {
        return ricettaIngredienteRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RicettaIngredienteResponse> getById(@PathVariable Long id) {
        RicettaIngrediente ri = ricettaIngredienteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Associazione con id " + id + " non trovata"));
        return ResponseEntity.ok(toResponse(ri));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<RicettaIngredienteResponse> create(@Valid @RequestBody RicettaIngredienteRequest request) {
        Ricetta ricetta = ricettaRepository.findById(request.ricettaId())
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + request.ricettaId() + " non trovata"));
        Ingrediente ingrediente = ingredienteRepository.findById(request.ingredienteId())
            .orElseThrow(() -> new NotFoundException("Ingrediente con id " + request.ingredienteId() + " non trovato"));

        RicettaIngrediente ri = new RicettaIngrediente();
        ri.setRicetta(ricetta);
        ri.setIngrediente(ingrediente);
        ri.setQuantita(request.quantita());
        ri.setUnita(request.unita());

        RicettaIngrediente salvato = ricettaIngredienteRepository.save(ri);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvato));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<RicettaIngredienteResponse> update(@PathVariable Long id,
            @Valid @RequestBody RicettaIngredienteRequest request) {
        RicettaIngrediente ri = ricettaIngredienteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Associazione con id " + id + " non trovata"));

        Ricetta ricetta = ricettaRepository.findById(request.ricettaId())
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + request.ricettaId() + " non trovata"));
        Ingrediente ingrediente = ingredienteRepository.findById(request.ingredienteId())
            .orElseThrow(() -> new NotFoundException("Ingrediente con id " + request.ingredienteId() + " non trovato"));

        ri.setRicetta(ricetta);
        ri.setIngrediente(ingrediente);
        ri.setQuantita(request.quantita());
        ri.setUnita(request.unita());

        RicettaIngrediente salvato = ricettaIngredienteRepository.save(ri);
        return ResponseEntity.ok(toResponse(salvato));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ricettaIngredienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
