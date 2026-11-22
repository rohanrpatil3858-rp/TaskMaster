package com.taskmaster.taskmaster.service;

import com.taskmaster.taskmaster.dto.ProjectRequest;
import com.taskmaster.taskmaster.dto.ProjectResponse;
import com.taskmaster.taskmaster.dto.UserResponse;
import com.taskmaster.taskmaster.entity.Project;
import com.taskmaster.taskmaster.entity.ProjectMember;
import com.taskmaster.taskmaster.entity.User;
import com.taskmaster.taskmaster.repository.ProjectMemberRepository;
import com.taskmaster.taskmaster.repository.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectMemberRepository memberRepository;
    private final UserService userService;

    public ProjectService(ProjectRepository projectRepository,
                          ProjectMemberRepository memberRepository,
                          UserService userService) {
        this.projectRepository = projectRepository;
        this.memberRepository = memberRepository;
        this.userService = userService;
    }

    @Transactional
    public ProjectResponse create(String email, ProjectRequest request) {
        User owner = userService.getUserByEmail(email);

        Project project = new Project();
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setOwner(owner);
        projectRepository.save(project);

        // The owner is also a member, so membership checks are the same for everyone.
        saveMember(project, owner);

        return ProjectResponse.from(project);
    }

    public List<ProjectResponse> listMine(String email) {
        User user = userService.getUserByEmail(email);
        return memberRepository.findByUserId(user.getId()).stream()
                .map(member -> ProjectResponse.from(member.getProject()))
                .toList();
    }

    public ProjectResponse get(Long projectId, String email) {
        User user = userService.getUserByEmail(email);
        return ProjectResponse.from(requireMember(projectId, user));
    }

    public UserResponse addMember(Long projectId, String requesterEmail, String newMemberEmail) {
        User requester = userService.getUserByEmail(requesterEmail);
        Project project = getProject(projectId);

        if (!project.getOwner().getId().equals(requester.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the project owner can add members");
        }

        User newMember = userService.getUserByEmail(newMemberEmail);
        if (isMember(projectId, newMember)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User is already a member of this project");
        }

        saveMember(project, newMember);
        return UserResponse.from(newMember);
    }

    public List<UserResponse> listMembers(Long projectId, String email) {
        User user = userService.getUserByEmail(email);
        requireMember(projectId, user);
        return memberRepository.findByProjectId(projectId).stream()
                .map(member -> UserResponse.from(member.getUser()))
                .toList();
    }

    public Project getProject(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    public boolean isMember(Long projectId, User user) {
        return memberRepository.existsByProjectIdAndUserId(projectId, user.getId());
    }

    // Returns the project, or throws 404 / 403 if it does not exist or the user is not in it.
    public Project requireMember(Long projectId, User user) {
        Project project = getProject(projectId);
        if (!isMember(projectId, user)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not a member of this project");
        }
        return project;
    }

    private void saveMember(Project project, User user) {
        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        memberRepository.save(member);
    }
}
