package com.taskmaster.taskmaster.dto;

import com.taskmaster.taskmaster.entity.TaskPriority;
import com.taskmaster.taskmaster.entity.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// Used for both creating and updating a task. Optional fields keep their current value when null.
@Getter
@Setter
@NoArgsConstructor
public class TaskRequest {

    @NotBlank(message = "title cannot be blank")
    private String title;

    @Size(max = 2000, message = "description must be at most 2000 characters")
    private String description;

    private LocalDate dueDate;

    private TaskPriority priority;

    private TaskStatus status;

    private String assignedToEmail;
}
