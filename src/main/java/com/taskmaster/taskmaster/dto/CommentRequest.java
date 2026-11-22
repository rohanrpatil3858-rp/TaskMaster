package com.taskmaster.taskmaster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRequest {

    @NotBlank(message = "text cannot be blank")
    @Size(max = 2000, message = "text must be at most 2000 characters")
    private String text;
}
