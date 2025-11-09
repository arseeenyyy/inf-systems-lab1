package com.github.arseeenyyy.dto.user;


import com.github.arseeenyyy.models.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequestDto {
    @NotBlank
    private String username;

    @NotBlank
    private String password;

    @NotNull
    private Role role;
}
