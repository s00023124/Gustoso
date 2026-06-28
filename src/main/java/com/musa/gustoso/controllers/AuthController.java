package com.musa.gustoso.controllers;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.musa.gustoso.dto.LoginRequest;
import com.musa.gustoso.dto.LoginResponse;
import com.musa.gustoso.dto.UserRequest;
import com.musa.gustoso.dto.UserResponse;
import com.musa.gustoso.entities.User;
import com.musa.gustoso.enums.Ruolo;
import com.musa.gustoso.exceptions.ConflictException;
import com.musa.gustoso.exceptions.NotFoundException;
import com.musa.gustoso.repositories.UserRepository;
import com.musa.gustoso.security.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final String AVATAR_DEFAULT =
        "https://res.cloudinary.com/ykilnu2e/image/upload/v1782651045/gustoso_avatar_tc66zs.png";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRequest request) {

        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new ConflictException("Email già registrata, usa un altro indirizzo email o accedi al tuo account con: " + request.email());
        }
        if (userRepository.findByTelefono(request.telefono()).isPresent()) {
            throw new ConflictException("Numero già registrato, usa un altro numero o accedi al tuo account");
        }
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new ConflictException("Username già in utilizzo, scegli un altro username");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setTelefono(request.telefono());
        user.setPassword(passwordEncoder.encode(request.password()));
        // Se un user non inserisce un'immagine, si usa un avatar generico di default
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

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

       
        User user = userRepository.findByUsername(request.username())
            .orElseThrow(() -> new NotFoundException("Credenziali non valide"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new NotFoundException("Credenziali non valide");
        }

        String token = jwtUtils.generaToken(user);

        LoginResponse response = new LoginResponse(
            token,
            user.getUsername(),
            user.getRuolo().name()
        );

        return ResponseEntity.ok(response);
    }
}