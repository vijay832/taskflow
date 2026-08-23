package com.taskflow.dto;

import com.taskflow.model.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record TaskDto(
        Long id,
        @NotBlank String title,
        String description,
        TaskStatus status,
        LocalDate dueDate
) {}
