package com.taskmaster.taskmaster.dto;

import com.taskmaster.taskmaster.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String text;
    private String authorEmail;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(comment.getId(), comment.getText(),
                comment.getAuthor().getEmail(), comment.getCreatedAt());
    }
}
