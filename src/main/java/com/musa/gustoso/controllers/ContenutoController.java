package com.musa.gustoso.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.musa.gustoso.dto.ContenutoResponse;
import com.musa.gustoso.entities.Articolo;
import com.musa.gustoso.entities.Contenuto;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.ContenutoRepository;

@RestController
@RequestMapping("/contenuti")
public class ContenutoController {

    private final ContenutoRepository contenutoRepository;

    public ContenutoController(ContenutoRepository contenutoRepository) {
        this.contenutoRepository = contenutoRepository;
    }

    // Converte un'entità Contenuto in DTO, includendo il tipo
    private ContenutoResponse toResponse(Contenuto c) {
        String tipo = (c instanceof Articolo) ? "ARTICOLO" : "RICETTA";
        return new ContenutoResponse(
            c.getId(),
            tipo,
            c.getTitolo(),
            c.getDataPubblicazione(),
            c.getCopertina(),
            c.getAutore().getUsername()
        );
    }

    @GetMapping
    public List<ContenutoResponse> getAll() {
        return contenutoRepository.findAllOrdinatiPerData().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/ricette")
    public List<ContenutoResponse> soloRicette() {
        return contenutoRepository.findSoloRicette().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/articoli")
    public List<ContenutoResponse> soloArticoli() {
        return contenutoRepository.findSoloArticoli().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/cerca")
    public List<ContenutoResponse> cerca(@RequestParam String titolo) {
        return contenutoRepository.cercaPerTitolo(titolo).stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/per-autore/{username}")
    public List<ContenutoResponse> perAutore(@PathVariable String username) {
        return contenutoRepository.findByAutoreUsername(username).stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContenutoResponse> getById(@PathVariable Long id) {
        Contenuto c = contenutoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Contenuto con id " + id + " non trovato"));
        return ResponseEntity.ok(toResponse(c));
    }
}
