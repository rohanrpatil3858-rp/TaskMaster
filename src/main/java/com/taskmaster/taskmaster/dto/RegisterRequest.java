package com.taskmaster.taskmaster.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "name cannot be blank")
    private String name;

    @NotBlank(message = "email cannot be blank")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank
    @Size(min = 8, message = "password must be at least 8 characters")
    private String password;
}
