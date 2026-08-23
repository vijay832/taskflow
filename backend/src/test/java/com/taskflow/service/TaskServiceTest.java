package com.taskflow.service;

import com.taskflow.dto.TaskDto;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.model.User;
import com.taskflow.repository.TaskRepository;
import com.taskflow.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock private TaskRepository taskRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private TaskService taskService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("vijay");
        when(userRepository.findByUsername("vijay")).thenReturn(Optional.of(user));
    }

    @Test
    void createTask_savesTaskOwnedByUser() {
        TaskDto dto = new TaskDto(null, "Write README", "Document the project", TaskStatus.TODO, null);
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        Task result = taskService.createTask("vijay", dto);

        assertEquals("Write README", result.getTitle());
        assertEquals(user, result.getOwner());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_deniesWhenNotOwner() {
        Task task = new Task();
        task.setId(5L);
        User otherUser = new User();
        otherUser.setUsername("someone-else");
        task.setOwner(otherUser);

        when(taskRepository.findById(5L)).thenReturn(Optional.of(task));

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> taskService.deleteTask("vijay", 5L));
    }
}
