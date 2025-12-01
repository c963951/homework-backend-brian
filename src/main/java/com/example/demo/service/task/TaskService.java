package com.example.demo.service.task;


import com.example.demo.model.task.entity.TaskEntity;
import com.example.demo.model.task.req.CreateTaskReq;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskService {
    TaskEntity createTask(CreateTaskReq task);
    TaskEntity getTask(String taskId);
    void cancelTask(String taskId);
    Page<TaskEntity> getPendingTasks(Pageable pageable) ;
    void triggerTask(TaskEntity task);
}
