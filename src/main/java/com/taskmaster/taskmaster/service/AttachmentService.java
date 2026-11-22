package com.taskmaster.taskmaster.service;

import com.taskmaster.taskmaster.dto.AttachmentResponse;
import com.taskmaster.taskmaster.entity.Attachment;
import com.taskmaster.taskmaster.entity.Task;
import com.taskmaster.taskmaster.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final TaskService taskService;
    private final UserService userService;
    private final Path uploadDir;

    public AttachmentService(AttachmentRepository attachmentRepository,
                             TaskService taskService,
                             UserService userService,
                             @Value("${app.upload-dir}") String uploadDir) {
        this.attachmentRepository = attachmentRepository;
        this.taskService = taskService;
        this.userService = userService;
        this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public AttachmentResponse upload(Long taskId, String email, MultipartFile file) throws IOException {
        Task task = taskService.getTaskForMember(taskId, email);

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }

        // The file is saved under a random name, so the client's file name can never escape the folder.
        String originalName = StringUtils.getFilename(StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "file" : file.getOriginalFilename()));
        Path target = uploadDir.resolve(UUID.randomUUID().toString());
        Files.createDirectories(uploadDir);
        file.transferTo(target);

        Attachment attachment = new Attachment();
        attachment.setFileName(originalName == null || originalName.isBlank() ? "file" : originalName);
        attachment.setContentType(file.getContentType());
        attachment.setStoredPath(target.toString());
        attachment.setTask(task);
        attachment.setUploadedBy(userService.getUserByEmail(email));

        return AttachmentResponse.from(attachmentRepository.save(attachment));
    }

    public List<AttachmentResponse> list(Long taskId, String email) {
        taskService.getTaskForMember(taskId, email);
        return attachmentRepository.findByTaskId(taskId).stream()
                .map(AttachmentResponse::from)
                .toList();
    }

    // Returns the attachment only if the user is a member of the task's project.
    public Attachment getForDownload(Long attachmentId, String email) {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Attachment not found"));
        taskService.getTaskForMember(attachment.getTask().getId(), email);
        return attachment;
    }
}
