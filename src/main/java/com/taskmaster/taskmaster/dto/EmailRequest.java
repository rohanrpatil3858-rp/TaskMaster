package com.taskmaster.taskmaster.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Used to add a project member and to assign a task.
@Getter
@Setter
@NoArgsConstructor
public class EmailRequest {

    @NotBlank(message = "email cannot be blank")
    @Email(message = "email must be valid")
    private String email;
}
