package com.taskmaster.taskmaster.dto;

import com.taskmaster.taskmaster.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String name;
    private String description;
    private String ownerEmail;

    public static ProjectResponse from(Project project) {
        return new ProjectResponse(project.getId(), project.getName(),
                project.getDescription(), project.getOwner().getEmail());
    }
}
