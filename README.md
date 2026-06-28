# Gustoso

Backend REST per una piattaforma di condivisione ricette. Permette agli utenti di pubblicare ricette, aggiungervi ingredienti e categorie, commentare e recensire i contenuti. Include autenticazione JWT con tre ruoli distinti, upload immagini su Cloudinary e dati nutrizionali via Spoonacular. Espone anche un'API GraphQL.

---

## Stack tecnologico

| Tecnologia | Versione |
|---|---|
| Java | 21 |
| Spring Boot | 4.1.0 |
| PostgreSQL | 17 |
| Spring Security + JWT (jjwt) | 0.12.6 |
| Cloudinary SDK | 1.39.0 |
| Spring GraphQL | incluso in Spring Boot 4 |

Il progetto include il Maven Wrapper (`mvnw`), quindi non è necessario avere Maven installato.

---

## Prerequisiti

- Java 21 installato
- PostgreSQL in esecuzione in locale
- Account Cloudinary gratuito → [cloudinary.com](https://cloudinary.com)
- Account Spoonacular gratuito → [spoonacular.com/food-api](https://spoonacular.com/food-api)

---

## Variabili d'ambiente

Il progetto non contiene segreti hardcoded. Tutte le credenziali vanno impostate come variabili d'ambiente di sistema prima di avviare l'applicazione.

| Variabile | Descrizione | Come ottenerla |
|---|---|---|
| `DB_PASSWORD` | Password dell'utente PostgreSQL | Impostata durante l'installazione di PostgreSQL |
| `JWT_SECRET` | Stringa segreta per firmare i token JWT (min. 32 caratteri) | Generala liberamente (es. una stringa casuale lunga) |
| `CLOUDINARY_CLOUD_NAME` | Nome del cloud Cloudinary | Dashboard Cloudinary → Account Details |
| `CLOUDINARY_API_KEY` | API Key Cloudinary | Dashboard Cloudinary → Account Details |
| `CLOUDINARY_API_SECRET` | API Secret Cloudinary | Dashboard Cloudinary → Account Details |
| `SPOONACULAR_API_KEY` | API Key Spoonacular | Dashboard Spoonacular → Profile → API Key |

### Impostazione su Windows (PowerShell)

```powershell
$env:DB_PASSWORD="la_tua_password"
$env:JWT_SECRET="una_stringa_segreta_molto_lunga_almeno_32_caratteri"
$env:CLOUDINARY_CLOUD_NAME="il_tuo_cloud_name"
$env:CLOUDINARY_API_KEY="la_tua_api_key"
$env:CLOUDINARY_API_SECRET="il_tuo_api_secret"
$env:SPOONACULAR_API_KEY="la_tua_api_key_spoonacular"
```

---

## Setup del database

Crea il database PostgreSQL con il nome `gustoso`:

```sql
CREATE DATABASE gustoso;
```

L'utente di default atteso è `postgres`. Se usi un utente diverso, modifica `spring.datasource.username` in `application.properties`.

Le tabelle vengono create automaticamente all'avvio grazie a `spring.jpa.hibernate.ddl-auto=update`. Non sono necessari script SQL.

---

## Avvio dell'applicazione

Con il Maven Wrapper incluso:

```bash
./mvnw spring-boot:run
```

Oppure, se hai Maven installato:

```bash
mvn spring-boot:run
```

L'applicazione sarà disponibile su http://localhost:8080.

---

## Dati di test (seeder automatico)

Al primo avvio, se il database è vuoto, un componente `DataSeeder` (`CommandLineRunner`) popola automaticamente alcuni dati di esempio:

- 5 utenti (1 ADMIN, 2 CREATOR, 2 USER) — vedi credenziali sotto
- 8 categorie su due assi (campo `tipo`): Nazionalità (Italiana, Messicana, Pakistana, Americana) e Tipologia di piatto (Primo piatto, Secondo piatto, Dolce, Contorno)
- 10 ingredienti: Tomato, Pasta, Cheese, Egg, Flour, Chicken, Basil, Garlic, Olive Oil, Sugar
- 10 ricette con titoli in inglese (per i dati nutrizionali Spoonacular), ognuna con autore, categorie e ingredienti
- 3 articoli a tema cucina
- 6 recensioni e 6 commenti sparsi su ricette e articoli

Il seeder si attiva solo se non ci sono utenti (`userRepository.count() == 0`), quindi riavviare l'applicazione non duplica i dati. Per ripopolare da zero, svuota il database (o ricrealo) e riavvia.

### Credenziali utenti di test

La password è la stessa per tutti: `password123` (salvata cifrata con BCrypt).

| Username | Password | Ruolo |
|---|---|---|
| `admin` | `password123` | ADMIN |
| `chef_anna` | `password123` | CREATOR |
| `chef_marco` | `password123` | CREATOR |
| `luca` | `password123` | USER |
| `sara` | `password123` | USER |

Per testare gli endpoint protetti basta fare `POST /auth/login` con uno di questi utenti e usare il token JWT restituito. L'utente `admin` ha già ruolo ADMIN: non serve alcuna promozione manuale.

---

## Come ottenere un utente ADMIN (solo senza seeder)

Con il seeder attivo questa procedura non è necessaria: usa direttamente l'utente `admin` (vedi sopra). Questi passi servono solo se parti da un database già popolato manualmente o se vuoi promuovere un utente registrato tramite `POST /auth/register` (che crea sempre utenti con ruolo USER).

Passo 1 — Registra un utente tramite Postman o la collezione inclusa:

```http
POST /auth/register
```

Passo 2 — Promuovilo ad ADMIN direttamente nel database:

```sql
UPDATE utenti SET ruolo = 'ADMIN' WHERE username = 'il_tuo_username';
```

Passo 3 — Effettua un nuovo login per ottenere un token JWT aggiornato:

```http
POST /auth/login
```

Il token JWT contiene il ruolo al momento della generazione. Dopo una promozione il vecchio token mantiene il ruolo precedente, quindi è necessario un nuovo login.

Per creare un utente con ruolo CREATOR:

```sql
UPDATE utenti SET ruolo = 'CREATOR' WHERE username = 'il_tuo_username';
```

---

## Funzionalità principali

- Gestione utenti — registrazione (con avatar di default se non viene fornita un'immagine), login JWT, aggiornamento profilo, upload immagine profilo su Cloudinary. Le risposte utente includono il ruolo.
- Ricette — CRUD completo, associazione a categorie e ingredienti, upload copertina su Cloudinary, paginazione, filtri e ordinamenti.
- Articoli — CRUD completo di contenuti editoriali che condividono la stessa struttura base delle ricette tramite ereditarietà JPA (`SINGLE_TABLE`).
- Categorie e Ingredienti — gestiti dall'admin, associabili alle ricette.
- Recensioni — voto da 1 a 5 con testo, calcolo automatico della media voti per ricetta.
- Commenti — su qualsiasi contenuto (ricetta o articolo).
- Autenticazione JWT — token stateless, tre ruoli distinti (USER / CREATOR / ADMIN).
- Autorizzazione granulare — `@PreAuthorize` sulle operazioni sensibili.
- Query avanzate — filtri per difficoltà, autore, categoria (per id e per nome), ingrediente, ricerca per titolo, ordinamento per data e tempo di preparazione, aggregazioni (AVG, COUNT).
- Query polimorfiche — endpoint `/contenuti` che sfrutta l'ereditarietà JPA con `TYPE()` per restituire ricette e articoli insieme.
- Cloudinary — upload reale di immagini (profilo utente e copertina ricetta).
- Spoonacular — dati nutrizionali (calorie, proteine, grassi, carboidrati). La ricerca avviene per titolo: poiché Spoonacular è un database internazionale, funziona con titoli riconosciuti (preferibilmente in inglese, es. "Carbonara", "Lasagna"). Con titoli non riconosciuti l'API non trova corrispondenze e viene restituito un 404 con messaggio esplicativo.
- GraphQL — endpoint alternativo al REST per interrogazioni flessibili sui dati.
- CORS — configurato e personalizzabile via `application.properties`.

---

## Endpoint principali

Per la lista completa con payload di esempio importa la collezione Postman inclusa nel repository: `Gustoso.postman_collection.json`

La collezione include tutte le request già configurate con le variabili `{{baseUrl}}` e `{{token}}`. Il token JWT viene salvato automaticamente dopo il login.

| Gruppo | Base path | Note |
|---|---|---|
| Autenticazione | `/auth` | `/register` e `/login` — pubblici |
| Utenti | `/utenti` | Gestione utenti (admin) + aggiornamento profilo |
| Ricette | `/ricette` | CRUD + filtri + statistiche + dati nutrizionali |
| Articoli | `/articoli` | CRUD articoli (lettura pubblica, scrittura CREATOR/ADMIN) |
| Contenuti | `/contenuti` | Ricette + articoli insieme (query polimorfiche) |
| Categorie | `/categorie` | Lettura pubblica, scrittura solo ADMIN |
| Ingredienti | `/ingredienti` | Lettura pubblica, scrittura solo ADMIN |
| Ricetta-Ingredienti | `/ricetta-ingredienti` | Associazioni ingrediente-quantità per ricetta |
| Recensioni | `/recensioni` | Voto (1-5) + testo su una ricetta |
| Commenti | `/commenti` | Testo libero su qualsiasi contenuto |

---

## Ruoli e permessi

| Operazione | USER | CREATOR | ADMIN |
|---|:---:|:---:|:---:|
| Registrazione / Login | Sì | Sì | Sì |
| Visualizza ricette, categorie, contenuti | Sì | Sì | Sì |
| Crea / modifica ricette | No | Sì | Sì |
| Carica copertina ricetta | No | Sì | Sì |
| Aggiunge ingredienti a ricette | No | Sì | Sì |
| Crea / modifica commenti e recensioni | Sì | Sì | Sì |
| Aggiorna un profilo utente ¹ | Sì | Sì | Sì |
| Gestisce categorie e ingredienti | No | No | Sì |
| Visualizza lista utenti | No | No | Sì |
| Elimina qualsiasi risorsa | No | No | Sì |

¹ L'aggiornamento del profilo (`PUT /utenti/{id}`) richiede solo l'autenticazione e non verifica che l'utente sia il proprietario del profilo: qualsiasi utente loggato può modificare un profilo per id. È una semplificazione consapevole del progetto. Lo stesso vale per la creazione di recensioni e commenti, dove l'`autoreId` arriva dal corpo della richiesta e non dal token.

---

## GraphQL

Oltre al REST, l'applicazione espone un'API GraphQL.

| Endpoint | Descrizione |
|---|---|
| `POST /graphql` | Endpoint principale per le query GraphQL |
| `GET /graphiql` | Interfaccia web interattiva per scrivere e testare query |

### Query disponibili

```graphql
ricette: [Ricetta]
ricetta(id: ID!): Ricetta
ricettePerDifficolta(difficolta: String!): [Ricetta]
ricettePerAutore(username: String!): [Ricetta]
utenti: [Utente]
utente(id: ID!): Utente
recensioniByRicetta(ricettaId: ID!): [Recensione]
contenuti: [Contenuto]
```

### Esempio di query

```graphql
{
  ricette {
    titolo
    difficolta
    autore
    categorie
  }
}
```

---

## Autore

Gustoso — Erika Panciroli
Computer Engineering & Artificial Intelligence BSc, EPICODE — 2026
