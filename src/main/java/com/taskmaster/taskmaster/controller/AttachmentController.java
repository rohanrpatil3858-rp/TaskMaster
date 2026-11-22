package com.taskmaster.taskmaster.controller;

import com.taskmaster.taskmaster.dto.AttachmentResponse;
import com.taskmaster.taskmaster.entity.Attachment;
import com.taskmaster.taskmaster.service.AttachmentService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/tasks/{taskId}/attachments")
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentResponse upload(Authentication authentication, @PathVariable Long taskId,
                                     @RequestParam("file") MultipartFile file) throws IOException {
        return attachmentService.upload(taskId, authentication.getName(), file);
    }

    @GetMapping("/tasks/{taskId}/attachments")
    public List<AttachmentResponse> list(Authentication authentication, @PathVariable Long taskId) {
        return attachmentService.list(taskId, authentication.getName());
    }

    @GetMapping("/attachments/{attachmentId}/download")
    public ResponseEntity<Resource> download(Authentication authentication, @PathVariable Long attachmentId) {
        Attachment attachment = attachmentService.getForDownload(attachmentId, authentication.getName());

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (attachment.getContentType() != null) {
            try {
                mediaType = MediaType.parseMediaType(attachment.getContentType());
            } catch (IllegalArgumentException ignored) {
                // Unknown content type: fall back to a plain download.
            }
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(attachment.getFileName()).build().toString())
                .body(new FileSystemResource(attachment.getStoredPath()));
    }
}
