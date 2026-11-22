package com.taskmaster.taskmaster.service;

import com.taskmaster.taskmaster.dto.CommentRequest;
import com.taskmaster.taskmaster.dto.CommentResponse;
import com.taskmaster.taskmaster.entity.Comment;
import com.taskmaster.taskmaster.entity.Task;
import com.taskmaster.taskmaster.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskService taskService;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository, TaskService taskService, UserService userService) {
        this.commentRepository = commentRepository;
        this.taskService = taskService;
        this.userService = userService;
    }

    public CommentResponse add(Long taskId, String email, CommentRequest request) {
        Task task = taskService.getTaskForMember(taskId, email);

        Comment comment = new Comment();
        comment.setTask(task);
        comment.setAuthor(userService.getUserByEmail(email));
        comment.setText(request.getText());

        return CommentResponse.from(commentRepository.save(comment));
    }

    public List<CommentResponse> list(Long taskId, String email) {
        taskService.getTaskForMember(taskId, email);
        return commentRepository.findByTaskIdOrderByCreatedAtAsc(taskId).stream()
                .map(CommentResponse::from)
                .toList();
    }
}
