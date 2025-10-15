package com.github.arseeenyyy.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreateResponseDto {
    private Long teamId;    
    private Integer memberCount;
    
}
