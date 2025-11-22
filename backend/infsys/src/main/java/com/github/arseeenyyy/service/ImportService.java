package com.github.arseeenyyy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arseeenyyy.models.*;
import com.github.arseeenyyy.repository.ImportRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ImportService {

    @Inject
    private ImportRepository importRepository;

    @Inject
    private JwtService jwtService;

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public ImportOperation processImport(InputStream fileInputStream, String jwtToken) {
        ImportOperation op = new ImportOperation();
        op.setTimestamp(LocalDateTime.now());
        
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = em.find(User.class, userId);
        op.setUser(user);

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(fileInputStream);

            List<JsonNode> dragons = new ArrayList<>();
            if (root.isArray()) {
                for (JsonNode obj : root) dragons.add(obj);
            } else {
                dragons.add(root);
            }

            int savedCount = 0;
            for (JsonNode dragonJson : dragons) {
                createDragon(dragonJson, user);
                savedCount++;
            }

            op.setStatus(ImportStatus.SUCCESS);
            op.setAddedCount(savedCount);

        } catch (Exception e) {
            op.setStatus(ImportStatus.FAILED);
            op.setAddedCount(0);
            op.setErrorMessage(e.getMessage());
            throw new RuntimeException("Import failed: " + e.getMessage());
        }

        importRepository.save(op);
        return op;
    }

    private void createDragon(JsonNode json, User user) {
        // Coordinates
        Coordinates coordinates = new Coordinates();
        coordinates.setX(json.get("coordinates").get("x").asDouble());
        coordinates.setY(json.get("coordinates").get("y").asDouble());
        coordinates.setUser(user);
        em.persist(coordinates);

        // Dragon
        Dragon dragon = new Dragon();
        dragon.setName(json.get("name").asText());
        dragon.setCoordinates(coordinates);
        dragon.setAge(json.get("age").asInt());
        dragon.setWeight(json.get("weight").asDouble());
        dragon.setUser(user);

        // Optional fields
        if (json.has("color")) {
            dragon.setColor(Color.valueOf(json.get("color").asText()));
        }
        if (json.has("character")) {
            dragon.setCharacter(DragonCharacter.valueOf(json.get("character").asText()));
        }

        em.persist(dragon);
    }

    public List<ImportOperation> getImportHistory(String jwtToken, int page, int size) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        String role = jwtService.getRoleFromToken(jwtToken);
        
        if ("ADMIN".equals(role)) {
            return importRepository.findAllPaged(page, size);
        } else {
            return importRepository.findByUserIdPaged(userId, page, size);
        }
    }

    public long getImportHistoryCount(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        String role = jwtService.getRoleFromToken(jwtToken);
        
        if ("ADMIN".equals(role)) {
            return importRepository.countAll();
        } else {
            return importRepository.countByUserId(userId);
        }
    }
}