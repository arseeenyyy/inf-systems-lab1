package com.github.arseeenyyy.service;

import jakarta.enterprise.context.ApplicationScoped;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;

@ApplicationScoped
public class PasswordService {
    
    private static final String ALGORITHM = "MD5";
    
    public String generateSalt() {
        return UUID.randomUUID().toString().substring(0, 16);
    }
    
    public String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            String saltedPassword = password + salt;
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }
    
    public boolean checkPassword(String password, String salt, String hashedPassword) {
        String testHash = hashPassword(password, salt);
        return testHash.equals(hashedPassword);
    }
}