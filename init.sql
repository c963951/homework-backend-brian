SET NAMES utf8mb4;

-- DDL for scheduled_tasks table

CREATE TABLE tasks (
    -- 任務唯一識別碼 (Primary Key)
                       task_id VARCHAR(36) NOT NULL PRIMARY KEY COMMENT '任務的UUID或唯一識別碼',

    -- 任務預計執行時間 (Poller 查詢的關鍵欄位)
                       execute_at TIMESTAMP NOT NULL COMMENT '任務預計執行時間',

    -- 任務狀態 (例如: PENDING, TRIGGERED, CANCELLED)
                       status VARCHAR(20) NOT NULL COMMENT '任務狀態',

    -- 任務負載內容 (Payload), 使用 JSON 類型儲存 Map<String, Object>
                       payload_json JSON NULL COMMENT '發送給 MQ 的任務負載內容',

    -- 創建時間 (審計欄位)
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '記錄創建時間',

    -- 更新時間 (審計欄位，自動更新)
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '記錄最後更新時間',

    -- 核心索引：為了 Poller 批次查詢而建立
    -- Poller 查詢條件: WHERE execute_at <= NOW() AND status = 'PENDING'
                       INDEX idx_execute_status (execute_at, status)

) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_0900_ai_ci
  COMMENT='定時任務調度核心表';