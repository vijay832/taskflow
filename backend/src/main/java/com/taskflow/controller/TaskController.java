package com.taskflow.controller;

import com.taskflow.dto.TaskDto;
import com.taskflow.model.Task;
import com.taskflow.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getTasks(Authentication auth) {
        return taskService.getTasksForUser(auth.getName());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(Authentication auth, @Valid @RequestBody TaskDto dto) {
        return ResponseEntity.ok(taskService.createTask(auth.getName(), dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(Authentication auth, @PathVariable Long id, @Valid @RequestBody TaskDto dto) {
        return ResponseEntity.ok(taskService.updateTask(auth.getName(), id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(Authentication auth, @PathVariable Long id) {
        taskService.deleteTask(auth.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
