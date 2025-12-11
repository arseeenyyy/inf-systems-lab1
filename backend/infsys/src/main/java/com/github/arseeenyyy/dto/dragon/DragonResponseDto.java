package com.github.arseeenyyy.dto.dragon;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.github.arseeenyyy.dto.coordinates.CoordinatesResponseDto;
import com.github.arseeenyyy.dto.dragonCave.DragonCaveResponseDto;
import com.github.arseeenyyy.dto.dragonHead.DragonHeadResponseDto;
import com.github.arseeenyyy.dto.person.PersonResponseDto;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate creationDate;
    private DragonCaveResponseDto cave;
    private PersonResponseDto killer;
    private Integer age;
    private double weight;
    private Color color;
    private DragonCharacter character;
    private DragonHeadResponseDto head;
    private Long userId;
}