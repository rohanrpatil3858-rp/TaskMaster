package com.taskmaster.taskmaster.repository;

import com.taskmaster.taskmaster.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}
