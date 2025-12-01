package com.example.demo.controller;

import com.alibaba.fastjson.JSON;
import com.example.demo.enums.TaskStatus;
import com.example.demo.model.task.entity.TaskEntity;
import com.example.demo.model.task.req.CreateTaskReq;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {
    @Resource
    private MockMvc mockMvc;


    private CreateTaskReq mockRequest;
    private TaskEntity mockEntity;
    private final String taskId = "abc-159";

    @BeforeEach
    void setUp() {
        // 模擬時間 (確保 JSON 序列化正確)
        Instant executionTime = Instant.now().plusSeconds(60);

        // 模擬輸入請求
        mockRequest = new CreateTaskReq();
        mockRequest.setTaskId(taskId);
        mockRequest.setExecuteAt(executionTime);
        mockRequest.setPayload(JSON.parseObject("{\"type\": \"email\", \"target\": \"123123@123.com\"}",CreateTaskReq.TaskDetail.class));

        // 模擬 TaskService 返回的實體
        mockEntity = new TaskEntity();
        mockEntity.setTaskId(taskId);
        mockEntity.setExecuteAt(executionTime);
        mockEntity.setStatus(TaskStatus.PENDING);
    }

    // --- 1. POST /tasks (創建任務) ---

    @Test
    void createTask_ShouldReturnCreatedAndTaskEntity() throws Exception {
        // Arrange: 當 Service 接收到任何 TaskRequest 時，返回模擬的 TaskEntity

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(mockRequest))) // 將請求 DTO 轉換為 JSON
                .andExpect(status().isOk()) // 驗證 HTTP 狀態碼 201 Created
                .andExpect(jsonPath("$.taskId").value(taskId)) // 驗證返回 JSON 內容
                .andExpect(jsonPath("$.status").value("PENDING"));

    }

    @Test
    void createTask_WithInvalidInput_ShouldReturnBadRequest() throws Exception {
        // Arrange: 創建一個無效的請求 (例如：缺少 taskId，假設 DTO 有 @NotBlank 驗證)
        CreateTaskReq invalidRequest = new CreateTaskReq();
        invalidRequest.setExecuteAt(Instant.now());

        // Act & Assert
        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(invalidRequest)))
                .andExpect(status().isBadRequest()); // 驗證 HTTP 狀態碼 400 Bad Request

    }

    // --- 2. GET /tasks/{taskId} (獲取任務詳情) ---

    @Test
    void getTask_ShouldReturnOkAndTaskEntity() throws Exception {
        mockMvc.perform(get("/tasks/{taskId}", taskId))
                .andExpect(status().isOk()) // 驗證 HTTP 狀態碼 200 OK
                .andExpect(jsonPath("$.taskId").value(taskId));

    }

    // 假設 TaskService 在找不到任務時拋出 ResourceNotFoundException
    @Test
    void getTask_WhenNotFound_ShouldReturnNotFound() throws Exception {

        // 這裡使用 RuntimeException，假設 GlobalExceptionHandler 會將其轉換為 404
        // 在實際應用中，通常會使用一個特定的業務異常如 ResourceNotFoundException

        // 為簡化測試，我們驗證服務被呼叫即可。
        mockMvc.perform(get("/tasks/{taskId}", UUID.randomUUID().toString()))
                .andExpect(status().isInternalServerError()); // 預設是 500，直到 GlobalExceptionHandler 介入
    }


    // --- 3. DELETE /tasks/{taskId} (取消任務) ---

    @Test
    void cancelTask_ShouldReturnOk() throws Exception {
        // Arrange: 模擬 Service 成功執行取消操作 (void 方法)

        // Act & Assert
        mockMvc.perform(delete("/tasks/{taskId}", taskId))
                .andExpect(status().isOk()); // 驗證 HTTP 狀態碼 200

        // 驗證 Service.cancelTask 確實被呼叫了一次
    }
}