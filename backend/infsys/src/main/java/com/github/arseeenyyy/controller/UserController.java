package com.github.arseeenyyy.controller;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserController {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
