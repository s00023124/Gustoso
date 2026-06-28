package com.musa.gustoso.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.musa.gustoso.dto.CategoriaRequest;
import com.musa.gustoso.dto.CategoriaResponse;
import com.musa.gustoso.entities.Categoria;
import com.musa.gustoso.exceptions.ConflictException;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.CategoriaRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorie")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    @GetMapping
    public List<CategoriaResponse> getAll() {
        return categoriaRepository.findAll().stream()
            .map(categoria -> new CategoriaResponse(
                categoria.getId(),
                categoria.getNome(),
                categoria.getTipo()
            ))
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoriaResponse> getById(@PathVariable Long id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Categoria con id " + id + " non trovata, forse hai sbagliato ID?"));
        CategoriaResponse response = new CategoriaResponse(
            categoria.getId(),
            categoria.getNome(),
            categoria.getTipo()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> create(@Valid @RequestBody CategoriaRequest request) {

        if (categoriaRepository.findByNome(request.nome()).isPresent()) {
            throw new ConflictException("Categoria '" + request.nome() + "' gia esistente");
        }

        Categoria categoria = new Categoria();
        categoria.setNome(request.nome());
        categoria.setTipo(request.tipo());

        Categoria salvata = categoriaRepository.save(categoria);

        CategoriaResponse response = new CategoriaResponse(
            salvata.getId(),
            salvata.getNome(),
            salvata.getTipo()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaResponse> update(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Categoria con id " + id + " non trovata, forse hai sbagliato ID?"));
        categoria.setNome(request.nome());
        categoria.setTipo(request.tipo());

        Categoria salvata = categoriaRepository.save(categoria);
        CategoriaResponse response = new CategoriaResponse(
            salvata.getId(),
            salvata.getNome(),
            salvata.getTipo()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoriaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
