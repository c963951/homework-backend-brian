package com.example.demo.model.task.req;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class CreateTaskReq {
    @NotBlank
    private String taskId;
    @NotNull
    private Instant executeAt;
    @Valid
    @NotNull
    private TaskDetail payload;

    @Getter
    @Setter
    public static class TaskDetail {
        /**
         * ex: email, phone....
         */
        @NotBlank
        private String type;
        /**
         * depends on type, ex: type -> email, target -> xxx@aa.com; type -> phone , target -> +88621345641
         */
        @NotBlank
        private String target;
        /**
         * task memo
         */
        private String message;
    }
}
