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
                createDragonWithRelations(dragonJson, user);
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

    private void createDragonWithRelations(JsonNode json, User user) {
        Coordinates coordinates = new Coordinates();
        coordinates.setX(json.get("coordinates").get("x").asDouble());
        coordinates.setY(json.get("coordinates").get("y").asDouble());
        coordinates.setUser(user);
        em.persist(coordinates);

        DragonCave cave = null;
        if (json.has("cave") && !json.get("cave").isNull()) {
            cave = new DragonCave();
            cave.setNumberOfTreasures(json.get("cave").get("numberOfTreasures").asLong());
            cave.setUser(user);
            em.persist(cave);
        }

        DragonHead head = null;
        if (json.has("head") && !json.get("head").isNull()) {
            head = new DragonHead();
            head.setSize(json.get("head").get("size").asInt());
            if (json.get("head").has("eyesCount") && !json.get("head").get("eyesCount").isNull()) {
                head.setEyesCount(json.get("head").get("eyesCount").asInt());
            }
            head.setUser(user);
            em.persist(head);
        }

        Person killer = null;
        if (json.has("killer") && !json.get("killer").isNull()) {
            killer = createPerson(json.get("killer"), user);
        }

        Dragon dragon = new Dragon();
        dragon.setName(json.get("name").asText());
        dragon.setCoordinates(coordinates);
        dragon.setCave(cave);
        dragon.setKiller(killer);
        dragon.setAge(json.get("age").asInt());
        dragon.setWeight(json.get("weight").asDouble());
        dragon.setUser(user);

        if (json.has("color") && !json.get("color").isNull()) {
            dragon.setColor(Color.valueOf(json.get("color").asText()));
        }
        if (json.has("character") && !json.get("character").isNull()) {
            dragon.setCharacter(DragonCharacter.valueOf(json.get("character").asText()));
        }
        
        dragon.setHead(head);

        em.persist(dragon);
    }

    private Person createPerson(JsonNode personJson, User user) {
        Person person = new Person();
        
        person.setName(personJson.get("name").asText());
        person.setEyeColor(Color.valueOf(personJson.get("eyeColor").asText()));
        person.setHeight(personJson.get("height").asInt());
        person.setUser(user);

        if (personJson.has("hairColor") && !personJson.get("hairColor").isNull()) {
            person.setHairColor(Color.valueOf(personJson.get("hairColor").asText()));
        }
        if (personJson.has("nationality") && !personJson.get("nationality").isNull()) {
            person.setNationality(Country.valueOf(personJson.get("nationality").asText()));
        }

        if (personJson.has("location") && !personJson.get("location").isNull()) {
            Location location = createLocation(personJson.get("location"), user);
            person.setLocation(location);
        }

        em.persist(person);
        return person;
    }

    private Location createLocation(JsonNode locationJson, User user) {
        Location location = new Location();
        
        location.setX(locationJson.get("x").asInt());
        location.setName(locationJson.get("name").asText());
        location.setUser(user);

        if (locationJson.has("y")) {
            location.setY((float) locationJson.get("y").asDouble());
        }
        if (locationJson.has("z")) {
            location.setZ(locationJson.get("z").asLong());
        }

        em.persist(location);
        return location;
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