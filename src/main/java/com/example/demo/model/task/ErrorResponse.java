package com.example.demo.model.task;

import lombok.*;

import java.time.Instant;

@Data
@Builder
public class ErrorResponse {
    private String error;        // 錯誤代碼
    private String message;      // 錯誤說明
    private int status;          // http 狀態碼
    private Instant timestamp;   // 時間戳
    private String path;         // 請求路徑
}
