package com.musa.gustoso.controllers;

import java.time.LocalDate;
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

import com.musa.gustoso.dto.ArticoloRequest;
import com.musa.gustoso.dto.ArticoloResponse;
import com.musa.gustoso.entities.Articolo;
import com.musa.gustoso.entities.User;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.ArticoloRepository;
import com.musa.gustoso.repositories.UserRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/articoli")
public class ArticoloController {

    private final ArticoloRepository articoloRepository;
    private final UserRepository userRepository;

    public ArticoloController(ArticoloRepository articoloRepository, UserRepository userRepository) {
        this.articoloRepository = articoloRepository;
        this.userRepository = userRepository;
    }

    private ArticoloResponse toResponse(Articolo articolo) {
        return new ArticoloResponse(
            articolo.getId(),
            articolo.getTitolo(),
            articolo.getCopertina(),
            articolo.getTempoDiLettura(),
            articolo.getCorpo(),
            articolo.getAutore().getUsername(),
            articolo.getDataPubblicazione()
        );
    }

    @GetMapping
    public List<ArticoloResponse> getAll() {
        return articoloRepository.findAll().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticoloResponse> getById(@PathVariable Long id) {
        Articolo articolo = articoloRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Articolo con id " + id + " non trovato, forse hai sbagliato ID?"));
        return ResponseEntity.ok(toResponse(articolo));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<ArticoloResponse> create(@Valid @RequestBody ArticoloRequest request) {
        Articolo articolo = new Articolo();
        articolo.setTitolo(request.titolo());
        articolo.setCopertina(request.copertina());
        articolo.setTempoDiLettura(request.tempoDiLettura());
        articolo.setCorpo(request.corpo());
        articolo.setDataPubblicazione(LocalDate.now()); 

        User autore = userRepository.findById(request.autoreId())
            .orElseThrow(() -> new NotFoundException("Autore con id " + request.autoreId() + " non trovato"));
        articolo.setAutore(autore);

        Articolo salvato = articoloRepository.save(articolo);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvato));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<ArticoloResponse> update(@PathVariable Long id, @Valid @RequestBody ArticoloRequest request) {
        Articolo articolo = articoloRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Articolo con id " + id + " non trovato, forse hai sbagliato ID?"));
        articolo.setTitolo(request.titolo());
        articolo.setCopertina(request.copertina());
        articolo.setTempoDiLettura(request.tempoDiLettura());
        articolo.setCorpo(request.corpo());

        Articolo salvato = articoloRepository.save(articolo);
        return ResponseEntity.ok(toResponse(salvato));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        articoloRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
