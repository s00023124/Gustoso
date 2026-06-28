package com.musa.gustoso.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.musa.gustoso.entities.User;


public interface UserRepository extends JpaRepository<User, Long>{ 
    Optional<User> findByEmail(String email);
    Optional<User> findByTelefono(String telefono);
    Optional<User> findByUsername(String username);
}