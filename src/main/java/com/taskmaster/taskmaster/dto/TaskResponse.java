package com.taskmaster.taskmaster.dto;

import com.taskmaster.taskmaster.entity.Task;
import com.taskmaster.taskmaster.entity.TaskPriority;
import com.taskmaster.taskmaster.entity.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private LocalDate dueDate;
    private TaskStatus status;
    private TaskPriority priority;
    private Long projectId;
    private String assignedToEmail;
    private String createdByEmail;

    public static TaskResponse from(Task task) {
        String assignee = task.getAssignedTo() == null ? null : task.getAssignedTo().getEmail();
        return new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getDueDate(),
                task.getStatus(), task.getPriority(), task.getProject().getId(),
                assignee, task.getCreatedBy().getEmail());
    }
}
