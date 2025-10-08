package com.github.arseeenyyy.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DragonHeadRequestDto {
    @NotNull(message = "size cannot be null")
    @Min(value = 1, message = "min size is 1")
    private int size;
    private Integer eyesCount;
}