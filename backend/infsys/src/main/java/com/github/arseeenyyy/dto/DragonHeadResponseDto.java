package com.github.arseeenyyy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
public class DragonHeadResponseDto {
    private long id;
    private int size;
    private Integer eyesCount;
}