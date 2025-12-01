package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration

public class ScheduleConfig implements SchedulingConfigurer {

    // 透過 application.yml 配置線程池大小
    @Value("${scheduler.pool-size:5}")
    private int poolSize;

    /**
     * 配置專門用於 TaskPoller 的線程池。
     * @param taskRegistrar 用於註冊排程任務的註冊器。
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();

        // 設置線程池大小 (本地開發通常 5 個線程足夠)
        taskScheduler.setPoolSize(poolSize);
        taskScheduler.setThreadNamePrefix("task-poller-");

        // 關鍵點：設置 TaskScheduler 的等待時間 (可選，但推薦)
        // Spring 會在銷毀該 Bean 時，等待 30 秒讓線程完成任務
        taskScheduler.setAwaitTerminationSeconds(30);
        taskScheduler.setWaitForTasksToCompleteOnShutdown(true);

        taskScheduler.initialize();
        taskRegistrar.setTaskScheduler(taskScheduler);
    }
}