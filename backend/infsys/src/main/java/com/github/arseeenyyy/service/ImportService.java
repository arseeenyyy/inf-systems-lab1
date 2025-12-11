package com.github.arseeenyyy.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.arseeenyyy.models.*;
import com.github.arseeenyyy.repository.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ImportService {

    @Inject private ImportRepository importRepository;
    @Inject private UserRepository userRepository;
    @Inject private CoordinatesRepository coordinatesRepository;
    @Inject private DragonCaveRepository dragonCaveRepository;
    @Inject private DragonHeadRepository dragonHeadRepository;
    @Inject private PersonRepository personRepository;
    @Inject private LocationRepository locationRepository;
    @Inject private DragonRepository dragonRepository;
    @Inject private JwtService jwtService;
    @Inject private MinioService minioService;

    /**
     * ДВУХФАЗНЫЙ КОММИТ:
     * Фаза 1: Сохранить файл в MinIO (ЛЮБОЙ файл)
     * Фаза 2: Сохранить в БД и импортировать данные
     * Если MinIO недоступен - НЕ создаем запись в БД вообще
     */
    public ImportOperation processImport(InputStream fileInputStream, String fileName, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        byte[] fileData;
        String fileKey = null;
        
        try {
            // Читаем файл
            fileData = fileInputStream.readAllBytes();
            
            // ========== ФАЗА 1: MinIO ==========
            try {
                fileKey = minioService.saveFile(
                    new ByteArrayInputStream(fileData), 
                    fileName, 
                    fileData.length
                );
            } catch (Exception e) {
                // MinIO недоступен - НЕ продолжаем, бросаем исключение
                throw new RuntimeException("MinIO недоступен: " + e.getMessage());
            }
            
            // ========== ФАЗА 2: БД ==========
            ImportOperation operation = new ImportOperation();
            operation.setUser(user);
            operation.setFileName(fileName);
            operation.setFileKey(fileKey);  // Всегда есть, если дошли сюда
            
            try {
                // Пробуем импортировать
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root;
                
                try {
                    root = mapper.readTree(new String(fileData));
                } catch (Exception e) {
                    // Невалидный JSON - всё равно сохраняем операцию
                    operation.setStatus(ImportStatus.FAILED);
                    operation.setErrorMessage("Невалидный JSON: " + e.getMessage());
                    operation.setAddedCount(0);
                    return importRepository.save(operation);
                }
                
                if (root == null || root.isEmpty()) {
                    operation.setStatus(ImportStatus.FAILED);
                    operation.setErrorMessage("empty file");
                    operation.setAddedCount(0);
                    return importRepository.save(operation);
                }

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

                operation.setStatus(ImportStatus.SUCCESS);
                operation.setAddedCount(savedCount);
                
            } catch (Exception e) {
                // Ошибка при импорте - всё равно сохраняем операцию (файл в MinIO)
                operation.setStatus(ImportStatus.FAILED);
                operation.setErrorMessage(e.getMessage());
                operation.setAddedCount(0);
            }
            
            // Сохраняем операцию в БД
            return importRepository.save(operation);
            
        } catch (RuntimeException e) {
            // MinIO недоступен - перебрасываем исключение, запись в БД НЕ создается
            throw e;
        } catch (Exception e) {
            // Другая ошибка
            throw new RuntimeException("Ошибка импорта: " + e.getMessage());
        }
    }

    public ImportOperation createFailedOperation(String errorMessage, String jwtToken) {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        User user = userRepository.findById(userId);
        
        ImportOperation op = new ImportOperation();
        op.setUser(user);
        op.setStatus(ImportStatus.FAILED);
        op.setErrorMessage(errorMessage);
        op.setAddedCount(0);
        op.setFileKey(null);
        
        return importRepository.save(op);
    }

    public InputStream downloadImportFile(Long importId, String jwtToken) throws Exception {
        Long userId = jwtService.getUserIdFromToken(jwtToken);
        String role = jwtService.getRoleFromToken(jwtToken);
        
        ImportOperation operation = importRepository.findById(importId);
        
        if (operation == null) {
            throw new RuntimeException("Import operation not found");
        }
        
        if (!operation.getUser().getId().equals(userId) && !"ADMIN".equals(role)) {
            throw new RuntimeException("Access denied");
        }
        
        if (operation.getFileKey() == null) {
            throw new RuntimeException("File not found for this operation");
        }
        
        return minioService.getFile(operation.getFileKey());
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

    // Остальные методы без изменений...
    private void createDragonWithRelations(JsonNode json, User user) {
        Coordinates coordinates = new Coordinates();
        try {
            coordinates.setX(json.get("coordinates").get("x").asDouble());
            coordinates.setY(json.get("coordinates").get("y").asDouble());
            coordinates.setUser(user);
            coordinatesRepository.save(coordinates);
        } catch (Exception e) {
            throw new RuntimeException("coordinates error: " + e.getMessage());
        }

        DragonCave cave = null;
        if (json.has("cave") && !json.get("cave").isNull()) {
            try {
                cave = new DragonCave();
                cave.setNumberOfTreasures(json.get("cave").get("numberOfTreasures").asLong());
                cave.setUser(user);
                dragonCaveRepository.save(cave);
            } catch (Exception e) {
                throw new RuntimeException("cave error: " + e.getMessage());
            }
        }

        DragonHead head = null;
        if (json.has("head") && !json.get("head").isNull()) {
            try {
                head = new DragonHead();
                head.setSize(json.get("head").get("size").asInt());
                if (json.get("head").has("eyesCount") && !json.get("head").get("eyesCount").isNull()) {
                    head.setEyesCount(json.get("head").get("eyesCount").asInt());
                }
                head.setUser(user);
                dragonHeadRepository.save(head);
            } catch (Exception e) {
                throw new RuntimeException("head error: " + e.getMessage());
            }
        }

        Person killer = null;
        if (json.has("killer") && !json.get("killer").isNull()) {
            try {
                killer = createPerson(json.get("killer"), user);
            } catch (Exception e) {
                throw new RuntimeException("killer error: " + e.getMessage());
            }
        }

        try {
            Dragon dragon = new Dragon();
            dragon.setName(json.get("name").asText());
            dragon.setCoordinates(coordinates);
            dragon.setCave(cave);
            dragon.setKiller(killer);
            dragon.setAge(json.get("age").asInt());
            dragon.setWeight(json.get("weight").asDouble());
            dragon.setUser(user);

            if (json.has("color") && !json.get("color").isNull()) {
                try {
                    dragon.setColor(Color.valueOf(json.get("color").asText().toUpperCase()));
                } catch (Exception e) {
                    throw new RuntimeException("color error: Invalid color value");
                }
            }
            if (json.has("character") && !json.get("character").isNull()) {
                try {
                    dragon.setCharacter(DragonCharacter.valueOf(json.get("character").asText().toUpperCase()));
                } catch (Exception e) {
                    throw new RuntimeException("character error: Invalid character value");
                }
            }
            
            dragon.setHead(head);
            dragonRepository.save(dragon);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("dragon error: " + e.getMessage());
        }
    }

    private Person createPerson(JsonNode personJson, User user) {
        try {
            Person person = new Person();
            
            person.setName(personJson.get("name").asText());
            
            try {
                person.setEyeColor(Color.valueOf(personJson.get("eyeColor").asText().toUpperCase()));
            } catch (Exception e) {
                throw new RuntimeException("eye color error: Invalid color value");
            }
            
            person.setHeight(personJson.get("height").asInt());
            person.setUser(user);

            if (personJson.has("hairColor") && !personJson.get("hairColor").isNull()) {
                try {
                    person.setHairColor(Color.valueOf(personJson.get("hairColor").asText().toUpperCase()));
                } catch (Exception e) {
                    throw new RuntimeException("hair color error: Invalid color value");
                }
            }
            if (personJson.has("nationality") && !personJson.get("nationality").isNull()) {
                try {
                    person.setNationality(Country.valueOf(personJson.get("nationality").asText().toUpperCase()));
                } catch (Exception e) {
                    throw new RuntimeException("nationality error: Invalid country value");
                }
            }

            if (personJson.has("location") && !personJson.get("location").isNull()) {
                try {
                    Location location = createLocation(personJson.get("location"), user);
                    person.setLocation(location);
                } catch (Exception e) {
                    throw new RuntimeException("location error: " + e.getMessage());
                }
            }

            personRepository.save(person);
            return person;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("person error: " + e.getMessage());
        }
    }

    private Location createLocation(JsonNode locationJson, User user) {
        try {
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
        } catch (Exception e) {
            throw new RuntimeException("location data error: " + e.getMessage());
        }
    }
}