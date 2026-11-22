package com.taskmaster.taskmaster.controller;

import com.taskmaster.taskmaster.dto.CommentRequest;
import com.taskmaster.taskmaster.dto.CommentResponse;
import com.taskmaster.taskmaster.dto.EmailRequest;
import com.taskmaster.taskmaster.dto.TaskRequest;
import com.taskmaster.taskmaster.dto.TaskResponse;
import com.taskmaster.taskmaster.entity.TaskStatus;
import com.taskmaster.taskmaster.service.CommentService;
import com.taskmaster.taskmaster.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;
    private final CommentService commentService;

    public TaskController(TaskService taskService, CommentService commentService) {
        this.taskService = taskService;
        this.commentService = commentService;
    }

    // Tasks assigned to the logged-in user. Same optional filters as the project task list.
    @GetMapping("/my")
    public List<TaskResponse> listAssignedToMe(Authentication authentication,
                                               @RequestParam(required = false) TaskStatus status,
                                               @RequestParam(required = false) String search,
                                               @RequestParam(required = false) String sortBy,
                                               @RequestParam(required = false) String direction) {
        return taskService.listAssignedToMe(authentication.getName(), status, search, sortBy, direction);
    }

    @GetMapping("/{taskId}")
    public TaskResponse get(Authentication authentication, @PathVariable Long taskId) {
        return taskService.get(taskId, authentication.getName());
    }

    @PutMapping("/{taskId}")
    public TaskResponse update(Authentication authentication, @PathVariable Long taskId,
                               @Valid @RequestBody TaskRequest request) {
        return taskService.update(taskId, authentication.getName(), request);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(Authentication authentication, @PathVariable Long taskId) {
        taskService.delete(taskId, authentication.getName());
    }

    @PatchMapping("/{taskId}/complete")
    public TaskResponse complete(Authentication authentication, @PathVariable Long taskId) {
        return taskService.complete(taskId, authentication.getName());
    }

    @PutMapping("/{taskId}/assign")
    public TaskResponse assign(Authentication authentication, @PathVariable Long taskId,
                               @Valid @RequestBody EmailRequest request) {
        return taskService.assign(taskId, authentication.getName(), request);
    }

    @PostMapping("/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse addComment(Authentication authentication, @PathVariable Long taskId,
                                      @Valid @RequestBody CommentRequest request) {
        return commentService.add(taskId, authentication.getName(), request);
    }

    @GetMapping("/{taskId}/comments")
    public List<CommentResponse> listComments(Authentication authentication, @PathVariable Long taskId) {
        return commentService.list(taskId, authentication.getName());
    }
}
