package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.VerterDTO;
import ru.school.mappers.VerterMapper;
import ru.school.model.entity.Verter;
import ru.school.services.CheckService;
import ru.school.services.VerterService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class VerterController {

    private final VerterService verterService;
    private final CheckService checkService;

    private final VerterMapper verterMapper;

    @GetMapping("/verter")
    public String findAll(@NotNull Model model) {
        model.addAttribute("verter", verterService.findAll());
        model.addAttribute("checks", checkService.findAll());
        return "verter";
    }

    @PostMapping("/verter/delete")
    public String delete(Long id) {
        verterService.delete(id);
        return "redirect:/verter";
    }

    @PostMapping("/verter/add")
    public String add(Verter verter) {
        verterService.save(verter);
        return "redirect:/verter";
    }

    @GetMapping("updates/verter/{id}")
    public String edit(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("verter", verterService.findById(id).get());
        model.addAttribute("checks", checkService.findAll());
        return "updates/verter";
    }

    @PostMapping("/verter/update")
    public String update(Verter verter) {
        verterService.update(verter);
        return "redirect:/verter";
    }

    @PostMapping(value = "verter/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        List<VerterDTO> verterListDTO = FileImportExport.importCsv(multipartFile, VerterDTO.class);
        verterService.saveAll(verterMapper.toEntities(verterListDTO));
        return "redirect:/verter";
    }

    @RequestMapping(path = "verter/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse, verterMapper.toDTOs(verterService.findAll()), VerterDTO.class);
    }
}
