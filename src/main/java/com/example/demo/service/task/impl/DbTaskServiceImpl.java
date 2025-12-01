package com.example.demo.service.task.impl;

import com.example.demo.model.task.entity.TaskEntity;
import com.example.demo.model.task.req.CreateTaskReq;
import com.example.demo.service.task.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

// db實作為當redis不可用時，線上刷動態配置做切換，採用方案是分節點做
// 每台機器有專屬的狀態，把任務放在內存裡做sortSet
// 新增時分配好該任務由哪台機器實作，直接存在那台的紀錄裡，並放置內存set
@Service("db")
public class DbTaskServiceImpl implements TaskService {
    @Override
    public TaskEntity createTask(CreateTaskReq request) {
        return null;
    }

    @Override
    public TaskEntity getTask(String taskId) {
        return null;
    }

    @Override
    public void cancelTask(String taskId) {

    }

    @Override
    public Page<TaskEntity> getPendingTasks(Pageable pageable) {
        return null;
    }

    @Override
    public void triggerTask(TaskEntity task) {

    }

}
