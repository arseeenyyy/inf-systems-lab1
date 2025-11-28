package com.github.arseeenyyy.dto.team;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamToCaveRequestDto {
    @NotNull
    private Long teamId;
    @NotNull
    private Long caveId;
}
