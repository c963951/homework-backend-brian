package com.example.demo.controller;

import com.example.demo.factory.task.TaskServiceFactory;
import com.example.demo.model.task.entity.TaskEntity;
import com.example.demo.model.task.req.CreateTaskReq;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskServiceFactory factory;

    public TaskController(TaskServiceFactory factory) {
        this.factory = factory;
    }

    @PostMapping
    public TaskEntity createTask(@Valid @RequestBody CreateTaskReq request) {
        return factory.getService().createTask(request);
    }


    @GetMapping("/{taskId}")
    public TaskEntity getTask(@PathVariable @NotBlank String taskId) {
        return factory.getService().getTask(taskId);
    }

    @DeleteMapping("/{taskId}")
    public void cancelTask(@PathVariable @NotBlank String taskId) {
        factory.getService().cancelTask(taskId);
    }

    @GetMapping
    public Page<TaskEntity> listPendingTasks(Pageable pageable) {
        return factory.getService().getPendingTasks(pageable);
    }
}