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

import com.musa.gustoso.dto.IngredienteRequest;
import com.musa.gustoso.dto.IngredienteResponse;
import com.musa.gustoso.entities.Ingrediente;
import com.musa.gustoso.exceptions.ConflictException;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.IngredienteRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/ingredienti")
public class IngredienteController {

    private final IngredienteRepository ingredienteRepository;

    public IngredienteController(IngredienteRepository ingredienteRepository) {
        this.ingredienteRepository = ingredienteRepository;
    }

    private IngredienteResponse toResponse(Ingrediente i) {
        return new IngredienteResponse(i.getId(), i.getNome());
    }

    @GetMapping
    public List<IngredienteResponse> getAll() {
        return ingredienteRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredienteResponse> getById(@PathVariable Long id) {
        Ingrediente i = ingredienteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ingrediente con id " + id + " non trovato"));
        return ResponseEntity.ok(toResponse(i));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IngredienteResponse> create(@Valid @RequestBody IngredienteRequest request) {
        if (ingredienteRepository.findByNome(request.nome()).isPresent()) {
            throw new ConflictException("Ingrediente '" + request.nome() + "' gia esistente");
        }
        Ingrediente i = new Ingrediente();
        i.setNome(request.nome());
        Ingrediente salvato = ingredienteRepository.save(i);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvato));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<IngredienteResponse> update(@PathVariable Long id, @Valid @RequestBody IngredienteRequest request) {
        Ingrediente i = ingredienteRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ingrediente con id " + id + " non trovato"));
        i.setNome(request.nome());
        Ingrediente salvato = ingredienteRepository.save(i);
        return ResponseEntity.ok(toResponse(salvato));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredienteRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
