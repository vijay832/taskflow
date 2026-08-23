package com.taskflow.service;

import com.taskflow.dto.TaskDto;
import com.taskflow.model.Task;
import com.taskflow.model.User;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<Task> getTasksForUser(String username) {
        User user = getUser(username);
        return taskRepository.findByOwner(user);
    }

    public Task createTask(String username, TaskDto dto) {
        User user = getUser(username);
        Task task = new Task();
        task.setTitle(dto.title());
        task.setDescription(dto.description());
        task.setStatus(dto.status() != null ? dto.status() : com.taskflow.model.TaskStatus.TODO);
        task.setDueDate(dto.dueDate());
        task.setOwner(user);
        return taskRepository.save(task);
    }

    public Task updateTask(String username, Long taskId, TaskDto dto) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        assertOwnership(task, username);

        task.setTitle(dto.title());
        task.setDescription(dto.description());
        if (dto.status() != null) task.setStatus(dto.status());
        task.setDueDate(dto.dueDate());
        return taskRepository.save(task);
    }

    public void deleteTask(String username, Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        assertOwnership(task, username);
        taskRepository.delete(task);
    }

    private void assertOwnership(Task task, String username) {
        if (!task.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not own this task");
        }
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }
}
