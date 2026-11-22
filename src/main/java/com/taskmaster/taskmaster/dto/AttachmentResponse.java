package com.taskmaster.taskmaster.dto;

import com.taskmaster.taskmaster.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class AttachmentResponse {

    private Long id;
    private String fileName;
    private String contentType;
    private String uploadedByEmail;
    private LocalDateTime uploadedAt;

    public static AttachmentResponse from(Attachment attachment) {
        return new AttachmentResponse(attachment.getId(), attachment.getFileName(), attachment.getContentType(),
                attachment.getUploadedBy().getEmail(), attachment.getUploadedAt());
    }
}
