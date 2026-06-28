package com.musa.gustoso.controllers;

import java.time.LocalDateTime;
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

import com.musa.gustoso.dto.CommentoRequest;
import com.musa.gustoso.dto.CommentoResponse;
import com.musa.gustoso.entities.Commento;
import com.musa.gustoso.entities.Contenuto;
import com.musa.gustoso.entities.User;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.CommentoRepository;
import com.musa.gustoso.repositories.ContenutoRepository;
import com.musa.gustoso.repositories.UserRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/commenti")
public class CommentoController {

    private final CommentoRepository commentoRepository;
    private final UserRepository userRepository;
    private final ContenutoRepository contenutoRepository;

    public CommentoController(CommentoRepository commentoRepository, UserRepository userRepository,
            ContenutoRepository contenutoRepository) {
        this.commentoRepository = commentoRepository;
        this.userRepository = userRepository;
        this.contenutoRepository = contenutoRepository;
    }

    private CommentoResponse toResponse(Commento c) {
        return new CommentoResponse(
            c.getId(),
            c.getAutore().getUsername(),
            c.getContenuto().getId(),
            c.getTesto(),
            c.getData()
        );
    }

    @GetMapping
    public List<CommentoResponse> getAll() {
        return commentoRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentoResponse> getById(@PathVariable Long id) {
        Commento c = commentoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Commento con id " + id + " non trovato"));
        return ResponseEntity.ok(toResponse(c));
    }

    @PostMapping
    public ResponseEntity<CommentoResponse> create(@Valid @RequestBody CommentoRequest request) {
        User autore = userRepository.findById(request.autoreId())
            .orElseThrow(() -> new NotFoundException("Autore con id " + request.autoreId() + " non trovato"));
        Contenuto contenuto = contenutoRepository.findById(request.contenutoId())
            .orElseThrow(() -> new NotFoundException("Contenuto con id " + request.contenutoId() + " non trovato"));

        Commento c = new Commento();
        c.setAutore(autore);
        c.setContenuto(contenuto);
        c.setTesto(request.testo());
        c.setData(LocalDateTime.now());

        Commento salvato = commentoRepository.save(c);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvato));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentoResponse> update(@PathVariable Long id, @Valid @RequestBody CommentoRequest request) {
        Commento c = commentoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Commento con id " + id + " non trovato"));

        c.setTesto(request.testo());

        Commento salvato = commentoRepository.save(c);
        return ResponseEntity.ok(toResponse(salvato));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
