package com.taskmaster.taskmaster.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProjectRequest {

    @NotBlank(message = "name cannot be blank")
    private String name;

    private String description;
}
