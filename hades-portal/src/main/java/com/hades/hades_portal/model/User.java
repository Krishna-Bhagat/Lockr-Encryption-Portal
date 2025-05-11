package com.hades.hades_portal.model;

import jakarta.persistence.*;
import lombok.Data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String publicKey; // for RSA

    @Column(columnDefinition = "TEXT")
    private String nValue;    // 'n' for RSA

    @Column(columnDefinition = "TEXT")
    private String privateKey; // for RSA

    private String passwordHash;

    public void setPassword(String rawPassword) {
        this.passwordHash = hashPassword(rawPassword);
    }

    public boolean checkPassword(String rawPassword) {
        return this.passwordHash.equals(hashPassword(rawPassword));
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
}