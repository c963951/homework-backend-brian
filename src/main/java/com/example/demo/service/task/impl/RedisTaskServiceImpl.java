package com.example.demo.service.task.impl;

import com.alibaba.fastjson.JSON;
import com.example.demo.consts.task.RedisKeyConst;
import com.example.demo.enums.TaskStatus;
import com.example.demo.exception.task.TaskException;
import com.example.demo.model.task.entity.TaskEntity;
import com.example.demo.model.task.req.CreateTaskReq;
import com.example.demo.repository.task.TaskRepository;
import com.example.demo.service.task.TaskService;
import lombok.RequiredArgsConstructor;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.apache.rocketmq.spring.support.RocketMQHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service("redis")
@RequiredArgsConstructor
public class RedisTaskServiceImpl implements TaskService {

    @Value("${task.schedule.topic:task-schedule-topic}")
    private String topic;

    private final RocketMQTemplate rocketMQTemplate;
    private final StringRedisTemplate redisTemplate;
    private final TaskRepository taskRepository;

    @Override
    @Transactional
    @CachePut(value = RedisKeyConst.TASK_REGION, key = "#req.taskId")
    public TaskEntity createTask(CreateTaskReq req) {
        Instant now = Instant.now();
        if (req.getExecuteAt().isBefore(now)) {
            throw new TaskException.InvalidTaskException("task execute at before now");
        }
        if (taskRepository.existsById(req.getTaskId())) {
            throw new TaskException.TaskAlreadyExistsException(req.getTaskId());
        }
        TaskEntity task = new TaskEntity();
        task.setTaskId(req.getTaskId());
        task.setExecuteAt(req.getExecuteAt());
        task.setPayloadJson(JSON.toJSONString(req.getPayload()));
        task.setStatus(TaskStatus.PENDING);
        TaskEntity savedTask = taskRepository.save(task);

        redisTemplate.opsForZSet().add(RedisKeyConst.TASK_DELAY_ZSET, req.getTaskId(), req.getExecuteAt().toEpochMilli());
        return savedTask;
    }

    @Override
    @Cacheable(value = RedisKeyConst.TASK_REGION, key = "#taskId")
    public TaskEntity getTask(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException.TaskNotFoundException("Task not found"));
    }

    @Override
    @Transactional // 確保 DB 狀態更新成功
    @CacheEvict(value = RedisKeyConst.TASK_REGION, key = "#taskId")
    public void cancelTask(String taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new TaskException.TaskNotFoundException("Task not found"));

        if (task.getStatus() == TaskStatus.TRIGGERED) {
            throw new TaskException.TaskAlreadyExistsException("Cannot cancel a triggered task");
        }

        // Remove from Redis
        redisTemplate.opsForZSet().remove(RedisKeyConst.TASK_DELAY_ZSET, taskId);

        // Update DB
        task.setStatus(TaskStatus.CANCELLED);
        task.setUpdatedAt(Instant.now());
        taskRepository.save(task);
    }

    @Override
    public Page<TaskEntity> getPendingTasks(Pageable pageable) {
        return taskRepository.findByStatus(TaskStatus.PENDING, pageable);
    }

    @Override
    @Transactional // 確保 DB 狀態更新成功
    @CacheEvict(value = RedisKeyConst.TASK_REGION, key = "#task.taskId") // 清除緩存
    public void triggerTask(TaskEntity task) {
        try {
            // 1. Build Message with Key
            Message<String> message = MessageBuilder.withPayload(task.getPayloadJson())
                    .setHeader(RocketMQHeaders.KEYS, task.getTaskId())
                    .build();

            // 採用 At-Least-Once 原則：先發送 MQ
            rocketMQTemplate.send(topic, message);

            // 2. 更新 DB 狀態
            task.setStatus(TaskStatus.TRIGGERED);
            task.setUpdatedAt(Instant.now());
            taskRepository.save(task);
        } catch (Exception e) {
            // 如果 DB 寫入失敗，事務會回滾，狀態仍為 PENDING。
            // mq resend
            throw new RuntimeException("Failed to complete task trigger process for " + task.getTaskId(), e);
        }
    }
}
