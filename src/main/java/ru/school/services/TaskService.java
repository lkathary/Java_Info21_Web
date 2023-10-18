package ru.school.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.Task;
import ru.school.repositories.TaskRepository;

import java.util.List;

@Service
public class TaskService extends BaseService<Task, String> {

    private final TaskRepository taskRepository;

    @Autowired
    protected TaskService(TaskRepository repository) {
        super(repository);
        taskRepository = repository;
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<String> getAllTitles() {
        return taskRepository.getAllTitles();
    }
}
