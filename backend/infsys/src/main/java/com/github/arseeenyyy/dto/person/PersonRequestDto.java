package com.github.arseeenyyy.dto.person;

import com.github.arseeenyyy.models.Color;
import com.github.arseeenyyy.models.Country;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonRequestDto {
    @NotNull(message = "name cannot be null")
    @NotBlank(message = "name cannot be blank")
    private String name;   

    @NotNull(message = "eye color cannot be null")
    private Color eyeColor;
    private Color hairColor;
    private Long locationId;

    @Min(value = 1, message = "min height value = 1") 
    @NotNull(message = "height cannot be null")
    private int height;
    private Country nationality;
}
