package com.github.arseeenyyy.dto;

import com.github.arseeenyyy.models.Color;
import com.github.arseeenyyy.models.DragonCharacter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DragonResponseDto {
    private Long id;
    private String name;
    private CoordinatesResponseDto coordinates;
    private java.time.LocalDate creationDate;
    private DragonCaveResponseDto cave;
    private PersonResponseDto killer;
    private Integer age;
    private double weight;
    private Color color;
    private DragonCharacter character;
    private DragonHeadResponseDto head;
}