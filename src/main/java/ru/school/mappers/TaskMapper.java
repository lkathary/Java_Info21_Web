package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.TaskDTO;
import ru.school.model.entity.Task;
import ru.school.services.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TaskMapper {

    private final TaskService taskService;
    public List<Task> toEntities(List<TaskDTO> taskListDTO) {
        List<Task> result = new ArrayList<>();
        for (TaskDTO it : taskListDTO) {
            if (taskService.findById(it.getParentTask()).isPresent() || it.getParentTask().equals("")) {
                result.add(new Task(
                        it.getTitle(),
                        it.getParentTask().equals("") ? null : taskService.findById(it.getParentTask()).get(),
                        it.getMaxExperience()));
            }
        }
        return result;
    }

    public Optional<Task> toEntity(TaskDTO taskDTO) {
        Task result = null;
            if (taskService.findById(taskDTO.getParentTask()).isPresent() || taskDTO.getParentTask().equals("")) {
                result = new Task(
                        taskDTO.getTitle(),
                        taskDTO.getParentTask().equals("") ? null : taskService.findById(taskDTO.getParentTask()).get(),
                        taskDTO.getMaxExperience());
            }
        return Optional.ofNullable(result);
    }

    public List<TaskDTO> toDTOs(List<Task> taskList) {
        return taskList.stream().map(x->new TaskDTO(
                                        x.getTitle(),
                                        x.getParentTask() == null ? "" : x.getParentTask().getTitle(),
                                        x.getMaxExperience()))
                                        .collect(Collectors.toList());
    }
}
