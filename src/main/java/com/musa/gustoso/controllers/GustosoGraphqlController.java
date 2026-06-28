package com.musa.gustoso.controllers;

import java.util.List;

import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import com.musa.gustoso.dto.ContenutoResponse;
import com.musa.gustoso.dto.RicettaIngredienteInfo;
import com.musa.gustoso.dto.RicettaResponse;
import com.musa.gustoso.dto.RecensioneResponse;
import com.musa.gustoso.dto.UserResponse;
import com.musa.gustoso.entities.Articolo;
import com.musa.gustoso.entities.Categoria;
import com.musa.gustoso.entities.Ricetta;
import com.musa.gustoso.entities.User;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.ContenutoRepository;
import com.musa.gustoso.repositories.RecensioneRepository;
import com.musa.gustoso.repositories.RicettaRepository;
import com.musa.gustoso.repositories.UserRepository;

// Remindser @Controller (non @RestController) perché GraphQL gestisce la serializzazione da solo
@Controller
public class GustosoGraphqlController {

    private final RicettaRepository ricettaRepository;
    private final UserRepository userRepository;
    private final RecensioneRepository recensioneRepository;
    private final ContenutoRepository contenutoRepository;

    public GustosoGraphqlController(RicettaRepository ricettaRepository,
            UserRepository userRepository, RecensioneRepository recensioneRepository,
            ContenutoRepository contenutoRepository) {
        this.ricettaRepository = ricettaRepository;
        this.userRepository = userRepository;
        this.recensioneRepository = recensioneRepository;
        this.contenutoRepository = contenutoRepository;
    }

    private RicettaResponse toRicettaResponse(Ricetta r) {
        List<String> nomiCategorie = r.getCategorie().stream()
            .map(Categoria::getNome).toList();
        List<RicettaIngredienteInfo> ingredienti = r.getRicettaIngredienti().stream()
            .map(ri -> new RicettaIngredienteInfo(
                ri.getIngrediente().getNome(), ri.getQuantita(), ri.getUnita()))
            .toList();
        return new RicettaResponse(r.getId(), r.getTitolo(), r.getCopertina(),
            r.getTempoPreparazione(), r.getDifficolta(), r.getProcedimento(),
            r.getAutore().getUsername(), nomiCategorie, r.getDataPubblicazione(), ingredienti);
    }

    @QueryMapping
    public List<RicettaResponse> ricette() {
        return ricettaRepository.findAll().stream().map(this::toRicettaResponse).toList();
    }

    @QueryMapping
    public RicettaResponse ricetta(@Argument Long id) {
        Ricetta r = ricettaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + id + " non trovata"));
        return toRicettaResponse(r);
    }

    @QueryMapping
    public List<RicettaResponse> ricettePerDifficolta(@Argument String difficolta) {
        return ricettaRepository.findByDifficolta(difficolta).stream()
            .map(this::toRicettaResponse).toList();
    }

    @QueryMapping
    public List<RicettaResponse> ricettePerAutore(@Argument String username) {
        return ricettaRepository.findByAutoreUsername(username).stream()
            .map(this::toRicettaResponse).toList();
    }

    @QueryMapping
    public List<UserResponse> utenti() {
        return userRepository.findAll().stream()
            .map(u -> new UserResponse(u.getId(), u.getUsername(), u.getEmail(),
                u.getTelefono(), u.getImmagineProfilo(), u.getDataIscrizione(), u.getRuolo().name()))
            .toList();
    }

    @QueryMapping
    public UserResponse utente(@Argument Long id) {
        User u = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));
        return new UserResponse(u.getId(), u.getUsername(), u.getEmail(),
            u.getTelefono(), u.getImmagineProfilo(), u.getDataIscrizione(), u.getRuolo().name());
    }

    @QueryMapping
    public List<RecensioneResponse> recensioniByRicetta(@Argument Long ricettaId) {
        return recensioneRepository.findAll().stream()
            .filter(r -> r.getRicetta().getId().equals(ricettaId))
            .map(r -> new RecensioneResponse(r.getId(), r.getAutore().getUsername(),
                r.getRicetta().getTitolo(), r.getVoto(), r.getTesto(), r.getData()))
            .toList();
    }

    @QueryMapping
    public List<ContenutoResponse> contenuti() {
        return contenutoRepository.findAllOrdinatiPerData().stream()
            .map(c -> new ContenutoResponse(c.getId(),
                (c instanceof Articolo) ? "ARTICOLO" : "RICETTA",
                c.getTitolo(), c.getDataPubblicazione(), c.getCopertina(),
                c.getAutore().getUsername()))
            .toList();
    }
}
