package com.taskmaster.taskmaster.repository;

import com.taskmaster.taskmaster.entity.ProjectMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {

    boolean existsByProjectIdAndUserId(Long projectId, Long userId);

    List<ProjectMember> findByUserId(Long userId);

    List<ProjectMember> findByProjectId(Long projectId);
}
