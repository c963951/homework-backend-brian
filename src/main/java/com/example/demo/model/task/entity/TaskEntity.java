package com.example.demo.model.task.entity;

import com.example.demo.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "tasks")
public class TaskEntity {
    @Id
    private String taskId;

    @Column(nullable = false)
    private Instant executeAt;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String payloadJson;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        if (status == null) status = TaskStatus.PENDING;
    }
}
