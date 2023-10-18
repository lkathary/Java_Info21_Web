package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.XpDTO;
import ru.school.mappers.XPMapper;
import ru.school.model.entity.XP;
import ru.school.services.CheckService;
import ru.school.services.XPService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class XPController {

    private final XPService xpService;
    private final CheckService checkService;

    private final XPMapper xpMapper;

    @GetMapping("/xp")
    public String findAll(@NotNull Model model) {
        model.addAttribute("xp", xpService.findAll());
        model.addAttribute("checks", checkService.findAll());
        return "xp";
    }

    @PostMapping("/xp/delete")
    public String delete(Long id) {
        xpService.delete(id);
        return "redirect:/xp";
    }

    @PostMapping("/xp/add")
    public String add(XP xp) {
        xpService.save(xp);
        return "redirect:/xp";
    }

    @GetMapping("updates/xp/{id}")
    public String edit(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("xp", xpService.findById(id).get());
        model.addAttribute("checks", checkService.findAll());
        return "updates/xp";
    }

    @PostMapping("/xp/update")
    public String update(XP xp) {
        xpService.update(xp);
        return "redirect:/xp";
    }

    @PostMapping(value = "xp/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        List<XpDTO> xpListDTO = FileImportExport.importCsv(multipartFile, XpDTO.class);
        xpService.saveAll(xpMapper.toEntities(xpListDTO));
        return "redirect:/xp";
    }

    @RequestMapping(path = "xp/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse, xpMapper.toDTOs(xpService.findAll()), XpDTO.class);
    }
}
