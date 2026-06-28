package com.musa.gustoso.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.musa.gustoso.entities.Articolo;
import com.musa.gustoso.entities.Categoria;
import com.musa.gustoso.entities.Commento;
import com.musa.gustoso.entities.Contenuto;
import com.musa.gustoso.entities.Ingrediente;
import com.musa.gustoso.entities.Recensione;
import com.musa.gustoso.entities.Ricetta;
import com.musa.gustoso.entities.RicettaIngrediente;
import com.musa.gustoso.entities.User;
import com.musa.gustoso.enums.Ruolo;
import com.musa.gustoso.repositories.ArticoloRepository;
import com.musa.gustoso.repositories.CategoriaRepository;
import com.musa.gustoso.repositories.CommentoRepository;
import com.musa.gustoso.repositories.IngredienteRepository;
import com.musa.gustoso.repositories.RecensioneRepository;
import com.musa.gustoso.repositories.RicettaIngredienteRepository;
import com.musa.gustoso.repositories.RicettaRepository;
import com.musa.gustoso.repositories.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final String PASSWORD_DEFAULT = "password123";

    // Avatar generico per tutti gli utenti di test
    private static final String AVATAR_DEFAULT =
        "https://res.cloudinary.com/ykilnu2e/image/upload/v1782651045/gustoso_avatar_tc66zs.png";

    private final UserRepository userRepository;
    private final CategoriaRepository categoriaRepository;
    private final IngredienteRepository ingredienteRepository;
    private final RicettaRepository ricettaRepository;
    private final ArticoloRepository articoloRepository;
    private final RicettaIngredienteRepository ricettaIngredienteRepository;
    private final RecensioneRepository recensioneRepository;
    private final CommentoRepository commentoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, CategoriaRepository categoriaRepository,
            IngredienteRepository ingredienteRepository, RicettaRepository ricettaRepository,
            ArticoloRepository articoloRepository, RicettaIngredienteRepository ricettaIngredienteRepository,
            RecensioneRepository recensioneRepository, CommentoRepository commentoRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.categoriaRepository = categoriaRepository;
        this.ingredienteRepository = ingredienteRepository;
        this.ricettaRepository = ricettaRepository;
        this.articoloRepository = articoloRepository;
        this.ricettaIngredienteRepository = ricettaIngredienteRepository;
        this.recensioneRepository = recensioneRepository;
        this.commentoRepository = commentoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Se esistono già utenti, il DB è già popolato, non succede nulla. 
        if (userRepository.count() > 0) {
            return;
        }

        // 1. UTENTI TEST creati in fase di seeding per testare la piattaforma
        User admin     = creaUtente("admin",      "admin@gustoso.it",   "3330000001", Ruolo.ADMIN);
        User chefAnna  = creaUtente("chef_anna",  "anna@gustoso.it",    "3330000002", Ruolo.CREATOR);
        User chefMarco = creaUtente("chef_marco", "marco@gustoso.it",   "3330000003", Ruolo.CREATOR);
        User luca      = creaUtente("luca",       "luca@gustoso.it",    "3330000004", Ruolo.USER);
        User sara      = creaUtente("sara",       "sara@gustoso.it",    "3330000005", Ruolo.USER);

        // 2. CATEGORIE 
        // Tipo categoria "Nazionalità"
        Categoria italiana   = creaCategoria("Italiana",   "Nazionalità");
        Categoria messicana  = creaCategoria("Messicana",  "Nazionalità");
        creaCategoria("Pakistana", "Nazionalità"); // categoria valida ma senza ricette nei dati di test
        Categoria americana  = creaCategoria("Americana",  "Nazionalità");
        // Tipo categoria"Tipologia di piatto"
        Categoria primoPiatto   = creaCategoria("Primo piatto",   "Tipologia di piatto");
        Categoria secondoPiatto = creaCategoria("Secondo piatto", "Tipologia di piatto");
        Categoria dolce         = creaCategoria("Dolce",          "Tipologia di piatto");
        Categoria contorno      = creaCategoria("Contorno",       "Tipologia di piatto");

        // 3. INGREDIENTI
        Ingrediente tomato   = creaIngrediente("Tomato");
        Ingrediente pasta    = creaIngrediente("Pasta");
        Ingrediente cheese   = creaIngrediente("Cheese");
        Ingrediente egg      = creaIngrediente("Egg");
        Ingrediente flour    = creaIngrediente("Flour");
        creaIngrediente("Chicken");
        Ingrediente basil    = creaIngrediente("Basil");
        Ingrediente garlic   = creaIngrediente("Garlic");
        Ingrediente oliveOil = creaIngrediente("Olive Oil");
        Ingrediente sugar    = creaIngrediente("Sugar");

        // 4. RICETTE (titoli in inglese/iternazionali così Spoonacular trova i dati nutrizionali)
        Ricetta carbonara = creaRicetta("Carbonara", "Media", 20,
            "Cuocere la pasta, mantecare con uova, pecorino e guanciale.", chefAnna, List.of(italiana, primoPiatto));
        aggiungiIngrediente(carbonara, pasta, 320, "g");
        aggiungiIngrediente(carbonara, egg, 4, "pz");
        aggiungiIngrediente(carbonara, cheese, 100, "g");

        Ricetta pizza = creaRicetta("Pizza", "Media", 90,
            "Stendere l'impasto, condire con pomodoro e mozzarella, cuocere in forno caldo.", chefMarco, List.of(italiana, secondoPiatto));
        aggiungiIngrediente(pizza, flour, 500, "g");
        aggiungiIngrediente(pizza, tomato, 200, "g");
        aggiungiIngrediente(pizza, cheese, 150, "g");

        Ricetta caesar = creaRicetta("Caesar Salad", "Facile", 15,
            "Mescolare insalata, pollo, crostini e salsa Caesar.", chefAnna, List.of(italiana, contorno));
        aggiungiIngrediente(caesar, cheese, 50, "g");

        Ricetta pancakes = creaRicetta("Pancakes", "Facile", 25,
            "Mescolare farina, uova e zucchero, cuocere in padella.", chefMarco, List.of(americana, dolce));
        aggiungiIngrediente(pancakes, flour, 250, "g");
        aggiungiIngrediente(pancakes, egg, 2, "pz");
        aggiungiIngrediente(pancakes, sugar, 50, "g");

        Ricetta tomatoSoup = creaRicetta("Tomato Soup", "Facile", 30,
            "Cuocere i pomodori con aglio, frullare e servire calda.", chefAnna, List.of(italiana, primoPiatto));
        aggiungiIngrediente(tomatoSoup, tomato, 600, "g");
        aggiungiIngrediente(tomatoSoup, garlic, 2, "spicchi");

        Ricetta lasagna = creaRicetta("Lasagna", "Difficile", 120,
            "Alternare sfoglie di pasta, ragù e besciamella, gratinare in forno.", chefMarco, List.of(italiana, primoPiatto));
        aggiungiIngrediente(lasagna, pasta, 400, "g");
        aggiungiIngrediente(lasagna, tomato, 300, "g");
        aggiungiIngrediente(lasagna, cheese, 200, "g");

        Ricetta cheesecake = creaRicetta("Cheesecake", "Media", 60,
            "Preparare la base di biscotti, aggiungere la crema di formaggio, raffreddare.", chefAnna, List.of(americana, dolce));
        aggiungiIngrediente(cheesecake, cheese, 400, "g");
        aggiungiIngrediente(cheesecake, sugar, 120, "g");
        aggiungiIngrediente(cheesecake, egg, 3, "pz");

        Ricetta risotto = creaRicetta("Risotto", "Media", 40,
            "Tostare il riso, aggiungere brodo poco alla volta, mantecare con formaggio.", chefMarco, List.of(italiana, primoPiatto));
        aggiungiIngrediente(risotto, cheese, 80, "g");
        aggiungiIngrediente(risotto, oliveOil, 30, "ml");

        Ricetta tiramisu = creaRicetta("Tiramisu", "Media", 45,
            "Alternare savoiardi inzuppati nel caffè e crema al mascarpone.", chefAnna, List.of(italiana, dolce));
        aggiungiIngrediente(tiramisu, cheese, 250, "g");
        aggiungiIngrediente(tiramisu, egg, 3, "pz");
        aggiungiIngrediente(tiramisu, sugar, 90, "g");

        Ricetta guacamole = creaRicetta("Guacamole", "Facile", 10,
            "Schiacciare l'avocado con aglio, lime e sale.", chefMarco, List.of(messicana, contorno));
        aggiungiIngrediente(guacamole, basil, 1, "spicchio");
        aggiungiIngrediente(guacamole, oliveOil, 15, "ml");

        // 5. ARTICOLI/BLOG x utenti piattaforma
        Articolo art1 = creaArticolo("5 segreti per una pasta perfetta",
            "Dalla scelta dell'acqua alla mantecatura: i trucchi degli chef per una pasta impeccabile.", 5, chefAnna);
        Articolo art2 = creaArticolo("La storia del tiramisù",
            "Le origini contese di uno dei dolci italiani più amati nel mondo.", 7, chefMarco);
        creaArticolo("Guida agli oli d'oliva",
            "Come riconoscere un buon olio extravergine e usarlo al meglio in cucina.", 4, chefAnna);

        // 6. RECENSIONI 
        creaRecensione(luca, carbonara, 5, "Cremosa al punto giusto, un classico!");
        creaRecensione(sara, carbonara, 4, "Buona, ma io ci metto meno pepe.");
        creaRecensione(luca, pizza, 5, "Sembra di stare a Napoli.");
        creaRecensione(sara, pancakes, 3, "Carini ma un po' troppo dolci per i miei gusti.");
        creaRecensione(luca, tiramisu, 5, "Il migliore che abbia mai mangiato.");
        creaRecensione(sara, lasagna, 4, "Ricca e saporita, porzione abbondante.");

        // 7. COMMENTI su ricette e articoli, autori diversi
        creaCommento(luca, carbonara, "Posso usare la pancetta al posto del guanciale?");
        creaCommento(sara, pizza, "Che tipo di farina consigliate?");
        creaCommento(luca, tomatoSoup, "Perfetta per le serate fredde!");
        creaCommento(sara, art1, "Articolo utilissimo, grazie!");
        creaCommento(luca, art2, "Non sapevo di queste origini, interessante.");
        creaCommento(sara, cheesecake, "La rifarò sicuramente per il weekend.");
    }

    private User creaUtente(String username, String email, String telefono, Ruolo ruolo) {
        User u = new User();
        u.setUsername(username);
        u.setEmail(email);
        u.setTelefono(telefono);
        u.setPassword(passwordEncoder.encode(PASSWORD_DEFAULT)); //reminder pswd nel DB ma hashata
        u.setImmagineProfilo(AVATAR_DEFAULT);
        u.setDataIscrizione(LocalDateTime.now());
        u.setRuolo(ruolo);
        return userRepository.save(u);
    }

    private Categoria creaCategoria(String nome, String tipo) {
        Categoria c = new Categoria();
        c.setNome(nome);
        c.setTipo(tipo);
        return categoriaRepository.save(c);
    }

    private Ingrediente creaIngrediente(String nome) {
        Ingrediente i = new Ingrediente();
        i.setNome(nome);
        return ingredienteRepository.save(i);
    }

    private Ricetta creaRicetta(String titolo, String difficolta, int tempoPreparazione,
            String procedimento, User autore, List<Categoria> categorie) {
        Ricetta r = new Ricetta();
        r.setTitolo(titolo);
        r.setDifficolta(difficolta);
        r.setTempoPreparazione(tempoPreparazione);
        r.setProcedimento(procedimento);
        r.setCopertina(null);
        r.setDataPubblicazione(LocalDate.now());
        r.setAutore(autore);
        r.setCategorie(categorie);
        return ricettaRepository.save(r);
    }

    private void aggiungiIngrediente(Ricetta ricetta, Ingrediente ingrediente, double quantita, String unita) {
        RicettaIngrediente ri = new RicettaIngrediente();
        ri.setRicetta(ricetta);
        ri.setIngrediente(ingrediente);
        ri.setQuantita(quantita);
        ri.setUnita(unita);
        ricettaIngredienteRepository.save(ri);
    }

    private Articolo creaArticolo(String titolo, String corpo, int tempoDiLettura, User autore) {
        Articolo a = new Articolo();
        a.setTitolo(titolo);
        a.setCorpo(corpo);
        a.setTempoDiLettura(tempoDiLettura);
        a.setCopertina(null);
        a.setDataPubblicazione(LocalDate.now());
        a.setAutore(autore);
        return articoloRepository.save(a);
    }

    private void creaRecensione(User autore, Ricetta ricetta, int voto, String testo) {
        Recensione r = new Recensione();
        r.setAutore(autore);
        r.setRicetta(ricetta);
        r.setVoto(voto);
        r.setTesto(testo);
        r.setData(LocalDateTime.now());
        recensioneRepository.save(r);
    }

    private void creaCommento(User autore, Contenuto contenuto, String testo) {
        Commento c = new Commento();
        c.setAutore(autore);
        c.setContenuto(contenuto);
        c.setTesto(testo);
        c.setData(LocalDateTime.now());
        commentoRepository.save(c);
    }
}
