package com.github.arseeenyyy.dto.dragon;

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
    @NotBlank(message = "Name is required and cannot be blank")
    private String name;

    @NotNull(message = "Coordinates ID is required")
    private Long coordinatesId;

    private Long caveId;

    private Long killerId;

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    private Integer age; 

    @Min(value = 1, message = "Weight must be at least 1") 
    private Double weight; 

    private Color color;

    private DragonCharacter character;
    private Long headId;
}