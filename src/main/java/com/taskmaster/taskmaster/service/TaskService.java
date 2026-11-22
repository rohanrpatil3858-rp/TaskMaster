package com.taskmaster.taskmaster.service;

import com.taskmaster.taskmaster.dto.EmailRequest;
import com.taskmaster.taskmaster.dto.TaskRequest;
import com.taskmaster.taskmaster.dto.TaskResponse;
import com.taskmaster.taskmaster.entity.Project;
import com.taskmaster.taskmaster.entity.Task;
import com.taskmaster.taskmaster.entity.TaskStatus;
import com.taskmaster.taskmaster.entity.User;
import com.taskmaster.taskmaster.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectService projectService;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository, ProjectService projectService, UserService userService) {
        this.taskRepository = taskRepository;
        this.projectService = projectService;
        this.userService = userService;
    }

    public TaskResponse create(Long projectId, String email, TaskRequest request) {
        User user = userService.getUserByEmail(email);
        Project project = projectService.requireMember(projectId, user);

        Task task = new Task();
        task.setProject(project);
        task.setCreatedBy(user);
        applyRequest(task, request);

        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse get(Long taskId, String email) {
        return TaskResponse.from(getTaskForMember(taskId, email));
    }

    public TaskResponse update(Long taskId, String email, TaskRequest request) {
        Task task = getTaskForMember(taskId, email);
        applyRequest(task, request);
        return TaskResponse.from(taskRepository.save(task));
    }

    public void delete(Long taskId, String email) {
        Task task = getTaskForMember(taskId, email);
        User user = userService.getUserByEmail(email);

        boolean isCreator = task.getCreatedBy().getId().equals(user.getId());
        boolean isOwner = task.getProject().getOwner().getId().equals(user.getId());
        if (!isCreator && !isOwner) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the task creator or the project owner can delete a task");
        }

        taskRepository.delete(task);
    }

    public TaskResponse complete(Long taskId, String email) {
        Task task = getTaskForMember(taskId, email);
        task.setStatus(TaskStatus.COMPLETED);
        return TaskResponse.from(taskRepository.save(task));
    }

    public TaskResponse assign(Long taskId, String email, EmailRequest request) {
        Task task = getTaskForMember(taskId, email);
        task.setAssignedTo(findAssignee(task.getProject(), request.getEmail()));
        return TaskResponse.from(taskRepository.save(task));
    }

    public List<TaskResponse> listAssignedToMe(String email, TaskStatus status, String search,
                                               String sortBy, String direction) {
        User user = userService.getUserByEmail(email);
        return filterAndSort(taskRepository.findByAssignedToId(user.getId()), status, search, sortBy, direction);
    }

    public List<TaskResponse> listForProject(Long projectId, String email, TaskStatus status, String search,
                                             String sortBy, String direction) {
        User user = userService.getUserByEmail(email);
        projectService.requireMember(projectId, user);
        return filterAndSort(taskRepository.findByProjectId(projectId), status, search, sortBy, direction);
    }

    // Used by comments and attachments too: returns the task only if the user is in its project.
    public Task getTaskForMember(Long taskId, String email) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
        User user = userService.getUserByEmail(email);
        projectService.requireMember(task.getProject().getId(), user);
        return task;
    }

    // Used for both create and update; optional fields keep their current value when not sent.
    private void applyRequest(Task task, TaskRequest request) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setDueDate(request.getDueDate());

        if (request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if (request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if (request.getAssignedToEmail() != null) {
            task.setAssignedTo(findAssignee(task.getProject(), request.getAssignedToEmail()));
        }
    }

    private User findAssignee(Project project, String assigneeEmail) {
        User assignee = userService.getUserByEmail(assigneeEmail);
        if (!projectService.isMember(project.getId(), assignee)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Assignee must be a member of the project");
        }
        return assignee;
    }

    // Filtering and sorting are done in Java to keep the code simple; fine for small data sets.
    private List<TaskResponse> filterAndSort(List<Task> tasks, TaskStatus status, String search,
                                             String sortBy, String direction) {
        Comparator<Task> comparator = buildComparator(sortBy);
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return tasks.stream()
                .filter(task -> status == null || task.getStatus() == status)
                .filter(task -> matchesSearch(task, search))
                .sorted(comparator)
                .map(TaskResponse::from)
                .toList();
    }

    private boolean matchesSearch(Task task, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String text = search.toLowerCase();
        boolean inTitle = task.getTitle().toLowerCase().contains(text);
        boolean inDescription = task.getDescription() != null
                && task.getDescription().toLowerCase().contains(text);
        return inTitle || inDescription;
    }

    private Comparator<Task> buildComparator(String sortBy) {
        String field = sortBy == null ? "id" : sortBy;
        return switch (field) {
            case "id" -> Comparator.comparing(Task::getId);
            case "title" -> Comparator.comparing(Task::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "dueDate" -> Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder()));
            case "priority" -> Comparator.comparing(Task::getPriority);
            case "status" -> Comparator.comparing(Task::getStatus);
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "sortBy must be one of: id, title, dueDate, priority, status");
        };
    }
}
