package com.musa.gustoso.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.musa.gustoso.dto.NutrizioneResponse;
import com.musa.gustoso.dto.RicettaIngredienteInfo;
import com.musa.gustoso.dto.StatisticheRicettaResponse;
import com.musa.gustoso.repositories.RecensioneRepository;

import com.musa.gustoso.dto.RicettaRequest;
import com.musa.gustoso.dto.RicettaResponse;
import com.musa.gustoso.entities.Categoria;
import com.musa.gustoso.entities.Ricetta;
import com.musa.gustoso.entities.User;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.CategoriaRepository;
import com.musa.gustoso.repositories.RicettaRepository;
import com.musa.gustoso.repositories.UserRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/ricette")
public class RicettaController {

    private final RicettaRepository ricettaRepository;
    private final UserRepository userRepository;
    private final CategoriaRepository categoriaRepository;
    private final Cloudinary cloudinary;
    private final RecensioneRepository recensioneRepository;

    @Value("${spoonacular.api-key}")
    private String spoonacularApiKey;

    private final RestClient restClient = RestClient.create();

    public RicettaController(RicettaRepository ricettaRepository, UserRepository userRepository,
            CategoriaRepository categoriaRepository, Cloudinary cloudinary,
            RecensioneRepository recensioneRepository) {
        this.ricettaRepository = ricettaRepository;
        this.userRepository = userRepository;
        this.categoriaRepository = categoriaRepository;
        this.cloudinary = cloudinary;
        this.recensioneRepository = recensioneRepository;
    }

    private RicettaResponse toResponse(Ricetta ricetta) {
        List<String> nomiCategorie = ricetta.getCategorie().stream()
            .map(Categoria::getNome)
            .toList();
        List<RicettaIngredienteInfo> ingredienti = ricetta.getRicettaIngredienti().stream()
            .map(ri -> new RicettaIngredienteInfo(
                ri.getIngrediente().getNome(),
                ri.getQuantita(),
                ri.getUnita()))
            .toList();
        return new RicettaResponse(
            ricetta.getId(),
            ricetta.getTitolo(),
            ricetta.getCopertina(),
            ricetta.getTempoPreparazione(),
            ricetta.getDifficolta(),
            ricetta.getProcedimento(),
            ricetta.getAutore().getUsername(),
            nomiCategorie,
            ricetta.getDataPubblicazione(),
            ingredienti
        );
    }

    @GetMapping
    public Page<RicettaResponse> getAll(
            @PageableDefault(size = 10, sort = "dataPubblicazione") Pageable pageable) {
        return ricettaRepository.findAll(pageable).map(this::toResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RicettaResponse> getById(@PathVariable Long id) {
        Ricetta ricetta = ricettaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + id + " non trovata, forse hai sbagliato ID?"));
        return ResponseEntity.ok(toResponse(ricetta));
    }

    @GetMapping("/cerca")
    public List<RicettaResponse> cercaPerTitolo(@RequestParam String titolo) {
        return ricettaRepository.findByTitoloContainingIgnoreCase(titolo).stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/per-difficolta/{difficolta}")
    public List<RicettaResponse> perDifficolta(@PathVariable String difficolta) {
        return ricettaRepository.findByDifficolta(difficolta).stream()
            .map(this::toResponse)
            .toList();
    }

     @GetMapping("/per-autore/{username}")
    public List<RicettaResponse> perAutore(@PathVariable String username) {
        return ricettaRepository.findByAutoreUsername(username).stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/per-categoria/{categoriaId}")
    public List<RicettaResponse> perCategoria(@PathVariable Long categoriaId) {
        return ricettaRepository.findByCategorieId(categoriaId).stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/per-nome-categoria/{nome}")
    public List<RicettaResponse> perNomeCategoria(@PathVariable String nome) {
        return ricettaRepository.findDistinctByCategorieNome(nome).stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/per-ingrediente/{nomeIngrediente}")
    public List<RicettaResponse> perIngrediente(@PathVariable String nomeIngrediente) {
        return ricettaRepository.findDistinctByRicettaIngredientiIngredienteNome(nomeIngrediente).stream()
            .map(this::toResponse)
            .toList();
    }

      @GetMapping("/per-data")
    public List<RicettaResponse> perData() {
        return ricettaRepository.findAllByOrderByDataPubblicazioneDesc().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/per-tempo")
    public List<RicettaResponse> perTempo() {
        return ricettaRepository.findAllByOrderByTempoPreparazioneAsc().stream()
            .map(this::toResponse)
            .toList();
    }

    @GetMapping("/{id}/stats")
    public ResponseEntity<StatisticheRicettaResponse> getStats(@PathVariable Long id) {
        Ricetta ricetta = ricettaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + id + " non trovata"));

        Double media = recensioneRepository.mediaVotiByRicettaId(id);
        long conteggio = recensioneRepository.countByRicettaId(id);

        return ResponseEntity.ok(new StatisticheRicettaResponse(
            ricetta.getId(),
            ricetta.getTitolo(),
            media,
            conteggio
        ));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<RicettaResponse> create(@Valid @RequestBody RicettaRequest request) {
        Ricetta ricetta = new Ricetta();
        ricetta.setTitolo(request.titolo());
        ricetta.setTempoPreparazione(request.tempoPreparazione());
        ricetta.setDifficolta(request.difficolta());
        ricetta.setProcedimento(request.procedimento());
        ricetta.setCopertina(request.copertina());
        ricetta.setDataPubblicazione(LocalDate.now());

        User autore = userRepository.findById(request.autoreId())
            .orElseThrow(() -> new NotFoundException("Autore con id " + request.autoreId() + " non trovato"));
        ricetta.setAutore(autore);

        if (request.categorieIds() != null && !request.categorieIds().isEmpty()) {
            List<Categoria> categorie = categoriaRepository.findAllById(request.categorieIds());
            ricetta.setCategorie(categorie);
        }

        Ricetta salvata = ricettaRepository.save(ricetta);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(salvata));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<RicettaResponse> update(@PathVariable Long id, @Valid @RequestBody RicettaRequest request) {
        Ricetta ricetta = ricettaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + id + " non trovata, forse hai sbagliato ID?"));
        ricetta.setTitolo(request.titolo());
        ricetta.setCopertina(request.copertina());
        ricetta.setTempoPreparazione(request.tempoPreparazione());
        ricetta.setDifficolta(request.difficolta());
        ricetta.setProcedimento(request.procedimento());

        if (request.categorieIds() != null) {
            List<Categoria> categorie = categoriaRepository.findAllById(request.categorieIds());
            ricetta.setCategorie(categorie);
        }

        Ricetta salvata = ricettaRepository.save(ricetta);
        return ResponseEntity.ok(toResponse(salvata));
    }

    // Cerca su API di Spoonacular i dati nutrizionali usando il titolo 
    @GetMapping("/{id}/nutrizione")
    public ResponseEntity<NutrizioneResponse> getNutrizione(@PathVariable Long id) {
        Ricetta ricetta = ricettaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + id + " non trovata"));

            Map<?, ?> risposta = restClient.get()
            .uri("https://api.spoonacular.com/recipes/complexSearch?query={titolo}&addRecipeNutrition=true&number=1&apiKey={key}",
                ricetta.getTitolo(), spoonacularApiKey)
            .retrieve()
            .body(Map.class);

        List<?> risultati = (List<?>) risposta.get("results");
        if (risultati == null || risultati.isEmpty()) {
            throw new NotFoundException("Nessun dato nutrizionale trovato per: " + ricetta.getTitolo());
        }

        Map<?, ?> primoRisultato = (Map<?, ?>) risultati.get(0);
        Map<?, ?> nutrition = (Map<?, ?>) primoRisultato.get("nutrition");
        List<?> nutrients = (List<?>) nutrition.get("nutrients");

        double calorie = 0, proteine = 0, grassi = 0, carboidrati = 0;
        for (Object n : nutrients) {
            Map<?, ?> nutriente = (Map<?, ?>) n;
            String nome = (String) nutriente.get("name");
            double quantita = ((Number) nutriente.get("amount")).doubleValue();
            switch (nome) {
                case "Calories"      -> calorie      = quantita;
                case "Protein"       -> proteine     = quantita;
                case "Fat"           -> grassi       = quantita;
                case "Carbohydrates" -> carboidrati  = quantita;
            }
        }

        return ResponseEntity.ok(new NutrizioneResponse(
            ricetta.getTitolo(),
            calorie,
            proteine,
            grassi,
            carboidrati
        ));
    }

    // Carica una nuova copertina su Cloudinary e salva l'URL 
    @PutMapping("/{id}/copertina")
    @PreAuthorize("hasAnyRole('CREATOR', 'ADMIN')")
    public ResponseEntity<RicettaResponse> uploadCopertina(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {

        Ricetta ricetta = ricettaRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Ricetta con id " + id + " non trovata"));

        // Salva il file su Cloqudinary nella cartella "gustoso/copertine"
        Map<?, ?> risultato = cloudinary.uploader().upload(
            file.getBytes(),
            Map.of("folder", "gustoso/copertine")
        );

        String url = (String) risultato.get("secure_url");
        ricetta.setCopertina(url);
        Ricetta salvata = ricettaRepository.save(ricetta);

        return ResponseEntity.ok(toResponse(salvata));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ricettaRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
