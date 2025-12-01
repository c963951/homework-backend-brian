package com.example.demo.repository.task;

import com.example.demo.enums.TaskStatus;
import com.example.demo.model.task.entity.TaskEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TaskRepository extends JpaRepository<TaskEntity, String> {
    Page<TaskEntity> findByStatus(TaskStatus status, Pageable pageable);

}
