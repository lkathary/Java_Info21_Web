package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.TaskDTO;
import ru.school.mappers.TaskMapper;
import ru.school.model.entity.Task;
import ru.school.services.TaskService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    @GetMapping("/tasks")
    public String findAll(@NotNull Model model) {
        model.addAttribute("tasks", taskService.findAll());
        return "tasks";
    }

    @PostMapping("/tasks/delete")
    public String delete(String title) {
        taskService.delete(title);
        return "redirect:/tasks";
    }

    @PostMapping("/tasks/add")
    public String add(Task task) {
        taskService.save(task);
        return "redirect:/tasks";
    }

    @GetMapping("updates/task/{id}")
    public String edit(@PathVariable String id, @NotNull Model model) {
        model.addAttribute("task", taskService.findById(id).get());
        model.addAttribute("titles", taskService.getAllTitles());
        return "updates/task";
    }

    @PostMapping("/tasks/update")
    public String update(Task task) {
        taskService.update(task);
        return "redirect:/tasks";
    }

//    @PostMapping(value = "tasks/upload")
//    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
//        List<TaskDTO> taskListDTO = FileImportExport.importCsv(multipartFile, TaskDTO.class);
////        System.out.println(taskListDTO);
//        taskService.saveAll(taskMapper.toEntities(taskListDTO));
//        return "redirect:/tasks";
//    }

    @PostMapping(value = "tasks/upload")
    public String uploadFileOneByOne(@RequestParam("file") MultipartFile multipartFile) {
        List<TaskDTO> taskListDTO = FileImportExport.importCsv(multipartFile, TaskDTO.class);
        for (TaskDTO it : taskListDTO){
            Optional<Task> result = taskMapper.toEntity(it);
            result.ifPresent(taskService::save);
        }
        return "redirect:/tasks";
    }
    @RequestMapping(path = "tasks/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse, taskMapper.toDTOs(taskService.findAll()), TaskDTO.class);
    }
}
