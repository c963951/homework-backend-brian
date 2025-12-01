package com.example.demo.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
//@RefreshScope
public class DynamicConfig {

    @Value("${task.service}")
    private String taskService;

}
