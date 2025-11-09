package com.github.arseeenyyy.dto.team;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamToCaveResponseDto {
    private Long treasuresFound;
    private Integer dragonsKilled;
}