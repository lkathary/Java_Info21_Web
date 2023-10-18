package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.CheckDTO;
import ru.school.mappers.CheckMapper;
import ru.school.model.entity.Check;
import ru.school.services.CheckService;
import ru.school.services.PeerService;
import ru.school.services.TaskService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class CheckController {

    private final CheckService checkService;
    private final PeerService peerService;
    private final TaskService taskService;

    private final CheckMapper checkMapper;

    @GetMapping("/checks")
    public String findAll(@NotNull Model model) {
        model.addAttribute("checks", checkService.findAll());
        model.addAttribute("peers", peerService.findAll());
        model.addAttribute("titles", taskService.getAllTitles());
        return "checks";
    }

    @PostMapping("/checks/delete")
    public String delete(Long id) {
        checkService.delete(id);
        return "redirect:/checks";
    }

    @PostMapping("/checks/add")
    public String add(Check check) {
        checkService.save(check);
        return "redirect:/checks";
    }

    @GetMapping("updates/check/{id}")
    public String edit(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("check", checkService.findById(id).get());
        model.addAttribute("peers", peerService.findAll());
        model.addAttribute("titles", taskService.getAllTitles());
        return "updates/check";
    }

    @PostMapping("/check/update")
    public String update(Check check) {
        checkService.update(check);
        return "redirect:/checks";
    }

    @PostMapping(value = "checks/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        List<CheckDTO> checkListDTO = FileImportExport.importCsv(multipartFile, CheckDTO.class);
        checkService.saveAll(checkMapper.toEntities(checkListDTO));
        return "redirect:/checks";
    }

    @RequestMapping(path = "checks/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse, checkMapper.toDTOs(checkService.findAll()), CheckDTO.class);
    }
}
