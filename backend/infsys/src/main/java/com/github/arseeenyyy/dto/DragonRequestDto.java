package com.github.arseeenyyy.dto;

import com.github.arseeenyyy.models.Color;
import com.github.arseeenyyy.models.DragonCharacter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor 
@AllArgsConstructor 
public class DragonRequestDto {
    @NotNull
    @NotBlank
    private String name;

    @NotNull 
    private Long coordinatesId;

    private Long caveId;

    private Long killerId;

    @NotNull
    @Min(1)
    private Integer age;

    @Min(1) 
    private double weight;

    private Color color;

    private DragonCharacter character;
    private Long headId;
}