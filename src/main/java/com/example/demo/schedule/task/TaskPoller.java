package com.example.demo.schedule.task;


import com.example.demo.consts.task.RedisKeyConst;
import com.example.demo.enums.TaskStatus;
import com.example.demo.factory.task.TaskServiceFactory;
import com.example.demo.model.task.entity.TaskEntity;
import com.example.demo.repository.task.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskPoller {

    private final RedisTemplate<String, String> redisTemplate;
    private final TaskServiceFactory factory;
    private final TaskRepository taskRepository;
    private final DefaultRedisScript<List> atomicFetchAndRemoveScript;

    @Scheduled(fixedDelay = 1000)
    public void pollTasks() {
        long now = Instant.now().toEpochMilli();

        // 1. **原子性操作:** 執行 Lua 腳本，取出並刪除到期任務 ID 列表
        List<String> taskIds = (List<String>) redisTemplate.execute(
                atomicFetchAndRemoveScript,
                List.of(RedisKeyConst.TASK_DELAY_ZSET),
                String.valueOf(now),
                "200"
        );

        if (taskIds != null && !taskIds.isEmpty()) {

            // 2. **優化：批量查詢 MySQL (一次性 IN 查詢)**
            // 這裡才是取得完整任務內容的高效方式。
            List<TaskEntity> dueTasks = taskRepository.findAllById(taskIds);

            for (TaskEntity task : dueTasks) {
                if (task.getStatus() == TaskStatus.PENDING) {
                    // 3. 處理任務
                    factory.getService().triggerTask(task);
                }
            }
        }
    }
}
