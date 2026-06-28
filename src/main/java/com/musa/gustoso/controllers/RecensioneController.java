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

import com.musa.gustoso.dto.RecensioneRequest;
import com.musa.gustoso.dto.RecensioneResponse;
import com.musa.gustoso.entities.Recensione;
import com.musa.gustoso.entities.Ricetta;
import com.musa.gustoso.entities.User;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.RecensioneRepository;
import com.musa.gustoso.repositories.RicettaRepository;
import com.musa.gustoso.repositories.UserRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/recensioni")
public class RecensioneController {

    private final RecensioneRepository recensioneRepository;
    private final UserRepository userRepository;
    private final RicettaRepository ricettaRepository;

    public RecensioneController(RecensioneRepository recensioneRepository, UserRepository userRepository,
            RicettaRepository ricettaRepository) {
        this.recensioneRepository = recensioneRepository;
        this.userRepository = userRepository;
        this.ricettaRepository = ricettaRepository;
    }

    private RecensioneResponse toResponse(Recensione r) {
        return new RecensioneResponse(
            r.getId(),
            r.getAutore().getUsername(),
            r.getRicetta().getTitolo(),
            r.getVoto(),
            r.getTesto(),
            r.getData()
        );
    }

    @GetMapping
    public List<RecensioneResponse> getAll() {
        return recensioneRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecensioneResponse> getById(@PathVariable Long id) {
        Recensione r = recensioneRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Recensione con id " + id + " non trovata"));
        return ResponseEntity.ok(toResponse(r));
    }

    @PostMapping
    public ResponseEntity<RecensioneResponse> create(@Valid @RequestBody RecensioneRequest request) {
        User autore = userRepository.findById(request.autoreId())
            .orElseThrow(() -> new NotFoundException("Autore con id " + request.autoreId() + " non trovato"));
        Ricetta ricetta = ricettaRepository.findById(request.ricettaId())
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + request.ricettaId() + " non trovata"));

        Recensione r = new Recensione();
        r.setAutore(autore);
        r.setRicetta(ricetta);
        r.setVoto(request.voto());
        r.setTesto(request.testo());
        r.setData(LocalDateTime.now());

        Recensione salvata = recensioneRepository.save(r);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvata));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecensioneResponse> update(@PathVariable Long id, @Valid @RequestBody RecensioneRequest request) {
        Recensione r = recensioneRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Recensione con id " + id + " non trovata"));

        Ricetta ricetta = ricettaRepository.findById(request.ricettaId())
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + request.ricettaId() + " non trovata"));

        r.setRicetta(ricetta);
        r.setVoto(request.voto());
        r.setTesto(request.testo());

        Recensione salvata = recensioneRepository.save(r);
        return ResponseEntity.ok(toResponse(salvata));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recensioneRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
