package com.musa.gustoso.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;


import com.musa.gustoso.dto.UserRequest;
import com.musa.gustoso.dto.UserResponse;
import com.musa.gustoso.dto.UserUpdateRequest;
import com.musa.gustoso.entities.User;
import com.musa.gustoso.enums.Ruolo;
import com.musa.gustoso.exceptions.BadRequestException;
import com.musa.gustoso.exceptions.ConflictException;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.UserRepository;

import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/utenti")
public class UserController {
    private static final String AVATAR_DEFAULT =
        "https://res.cloudinary.com/ykilnu2e/image/upload/v1782651045/gustoso_avatar_tc66zs.png";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Cloudinary cloudinary;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder, Cloudinary cloudinary) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.cloudinary = cloudinary;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
            .map(user -> new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getTelefono(),
                user.getImmagineProfilo(),
                user.getDataIscrizione(),
                user.getRuolo().name()
            ))
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato, forse hai sbagliato ID?"));
        UserResponse response = new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getTelefono(),
            user.getImmagineProfilo(),
            user.getDataIscrizione(),
            user.getRuolo().name()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest request){

        if (userRepository.findByEmail(request.email()).isPresent()){
            throw new ConflictException("Email già registrata, usa un altro indirizzo email o accedi al tuo account con:" + request.email());
        }

        if(userRepository.findByTelefono(request.telefono()).isPresent()){
            throw new ConflictException("Numero già registrato, usa un altro numero o accedi al tuo account");
        }

        if(userRepository.findByUsername(request.username()).isPresent()){
            throw new ConflictException("Username già in utilizzo, scegli un altro username");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setTelefono(request.telefono());
        user.setPassword(passwordEncoder.encode(request.password()));
        String immagine = (request.immagineProfilo() != null && !request.immagineProfilo().isBlank())
            ? request.immagineProfilo()
            : AVATAR_DEFAULT;
        user.setImmagineProfilo(immagine);
        user.setDataIscrizione(LocalDateTime.now());
        user.setRuolo(Ruolo.USER);

        User salvato = userRepository.save(user);

        UserResponse response = new UserResponse(
            salvato.getId(),
            salvato.getUsername(),
            salvato.getEmail(),
            salvato.getTelefono(),
            salvato.getImmagineProfilo(),
            salvato.getDataIscrizione(),
            salvato.getRuolo().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User con id " + id + " non trovato, forse hai sbagliato ID?"));
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setTelefono(request.telefono());
        user.setImmagineProfilo(request.immagineProfilo());
        

        User salvato = userRepository.save(user);

        UserResponse response = new UserResponse(
            salvato.getId(),
            salvato.getUsername(),
            salvato.getEmail(),
            salvato.getTelefono(),
            salvato.getImmagineProfilo(),
            salvato.getDataIscrizione(),
            salvato.getRuolo().name()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/immagine")
    public ResponseEntity<UserResponse> uploadImmagine(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Nessun file immagine fornito: allega un file nel campo 'file'");
        }

        User user = userRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Utente con id " + id + " non trovato"));

        Map<?, ?> risultato = cloudinary.uploader().upload(
            file.getBytes(),
            Map.of("folder", "gustoso/profili")
        );

        String url = (String) risultato.get("secure_url");
        user.setImmagineProfilo(url);
        User salvato = userRepository.save(user);

        UserResponse response = new UserResponse(
            salvato.getId(),
            salvato.getUsername(),
            salvato.getEmail(),
            salvato.getTelefono(),
            salvato.getImmagineProfilo(),
            salvato.getDataIscrizione(),
            salvato.getRuolo().name()
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
