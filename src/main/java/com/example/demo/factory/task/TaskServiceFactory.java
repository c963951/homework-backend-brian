package com.example.demo.factory.task;

import com.example.demo.config.DynamicConfig;
import com.example.demo.service.task.TaskService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TaskServiceFactory {

    private final Map<String, TaskService> serviceMap;
    private final DynamicConfig config;

    public TaskServiceFactory(Map<String, TaskService> serviceMap, DynamicConfig config) {
        this.serviceMap = serviceMap;
        this.config = config;
    }

    public TaskService getService() {
        TaskService s = serviceMap.get(config.getTaskService());
        if (s == null) throw new IllegalArgumentException("Unknown task service: " + config.getTaskService());
        return s;
    }
}
