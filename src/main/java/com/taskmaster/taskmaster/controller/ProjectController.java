package com.taskmaster.taskmaster.controller;

import com.taskmaster.taskmaster.dto.EmailRequest;
import com.taskmaster.taskmaster.dto.ProjectRequest;
import com.taskmaster.taskmaster.dto.ProjectResponse;
import com.taskmaster.taskmaster.dto.TaskRequest;
import com.taskmaster.taskmaster.dto.TaskResponse;
import com.taskmaster.taskmaster.dto.UserResponse;
import com.taskmaster.taskmaster.entity.TaskStatus;
import com.taskmaster.taskmaster.service.ProjectService;
import com.taskmaster.taskmaster.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final TaskService taskService;

    public ProjectController(ProjectService projectService, TaskService taskService) {
        this.projectService = projectService;
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse create(Authentication authentication, @Valid @RequestBody ProjectRequest request) {
        return projectService.create(authentication.getName(), request);
    }

    @GetMapping
    public List<ProjectResponse> listMine(Authentication authentication) {
        return projectService.listMine(authentication.getName());
    }

    @GetMapping("/{projectId}")
    public ProjectResponse get(Authentication authentication, @PathVariable Long projectId) {
        return projectService.get(projectId, authentication.getName());
    }

    @PostMapping("/{projectId}/members")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse addMember(Authentication authentication, @PathVariable Long projectId,
                                  @Valid @RequestBody EmailRequest request) {
        return projectService.addMember(projectId, authentication.getName(), request.getEmail());
    }

    @GetMapping("/{projectId}/members")
    public List<UserResponse> listMembers(Authentication authentication, @PathVariable Long projectId) {
        return projectService.listMembers(projectId, authentication.getName());
    }

    @PostMapping("/{projectId}/tasks")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(Authentication authentication, @PathVariable Long projectId,
                                   @Valid @RequestBody TaskRequest request) {
        return taskService.create(projectId, authentication.getName(), request);
    }

    // Optional query params: status, search (title or description), sortBy, direction (asc or desc).
    @GetMapping("/{projectId}/tasks")
    public List<TaskResponse> listTasks(Authentication authentication, @PathVariable Long projectId,
                                        @RequestParam(required = false) TaskStatus status,
                                        @RequestParam(required = false) String search,
                                        @RequestParam(required = false) String sortBy,
                                        @RequestParam(required = false) String direction) {
        return taskService.listForProject(projectId, authentication.getName(), status, search, sortBy, direction);
    }
}
