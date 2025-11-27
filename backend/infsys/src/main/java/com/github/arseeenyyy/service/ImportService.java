package com.github.arseeenyyy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arseeenyyy.models.*;
import com.github.arseeenyyy.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ImportService {

    @Inject
    private ImportRepository importRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private CoordinatesRepository coordinatesRepository;

    @Inject
    private DragonCaveRepository dragonCaveRepository;

    @Inject
    private DragonHeadRepository dragonHeadRepository;

    @Inject
    private PersonRepository personRepository;

    @Inject
    private LocationRepository locationRepository;

    @Inject
    private DragonRepository dragonRepository;

    @Inject
    private JwtService jwtService;

    public ImportOperation processImport(InputStream fileInputStream, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        ImportOperation op = new ImportOperation();
        op.setUserId(userId);

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
                createDragonWithRelations(dragonJson, userId);
                savedCount++;
            }

            op.setStatus(ImportStatus.SUCCESS);
            op.setAddedCount(savedCount); 

        } catch (Exception e) {
            op.setStatus(ImportStatus.FAILED);
            op.setAddedCount(null); 
        }

        return importRepository.save(op);
    }

    public ImportOperation createFailedOperation(String errorMessage, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        
        ImportOperation op = new ImportOperation();
        op.setUserId(userId);
        op.setStatus(ImportStatus.FAILED);
        op.setAddedCount(null); 
        
        return importRepository.save(op);
    }

    private void createDragonWithRelations(JsonNode json, Long userId) {
        User user = userRepository.findById(userId);

        Coordinates coordinates = new Coordinates();
        coordinates.setX(json.get("coordinates").get("x").asDouble());
        coordinates.setY(json.get("coordinates").get("y").asDouble());
        coordinates.setUser(user);
        coordinatesRepository.save(coordinates);

        DragonCave cave = null;
        if (json.has("cave") && !json.get("cave").isNull()) {
            cave = new DragonCave();
            cave.setNumberOfTreasures(json.get("cave").get("numberOfTreasures").asLong());
            cave.setUser(user);
            dragonCaveRepository.save(cave);
        }

        DragonHead head = null;
        if (json.has("head") && !json.get("head").isNull()) {
            head = new DragonHead();
            head.setSize(json.get("head").get("size").asInt());
            if (json.get("head").has("eyesCount") && !json.get("head").get("eyesCount").isNull()) {
                head.setEyesCount(json.get("head").get("eyesCount").asInt());
            }
            head.setUser(user);
            dragonHeadRepository.save(head);
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
        dragonRepository.save(dragon);
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

        personRepository.save(person);
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

        locationRepository.save(location);
        return location;
    }

    public List<ImportOperation> getImportHistory(String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        String role = jwtService.getRoleFromToken(jwtToken);
        
        if ("ADMIN".equals(role)) {
            return importRepository.findAll();
        } else {
            return importRepository.findByUserId(userId);
        }
    }
}