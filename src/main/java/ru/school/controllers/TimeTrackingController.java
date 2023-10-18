package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.TimeTrackingDTO;
import ru.school.mappers.TimeTrackingMapper;
import ru.school.model.entity.TimeTracking;
import ru.school.services.PeerService;
import ru.school.services.TimeTrackingService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TimeTrackingController {

    private final TimeTrackingService timeTrackingService;
    private final PeerService peerService;

    private final TimeTrackingMapper timeTrackingMapper;

    @GetMapping("/timetracking")
    public String findAll(@NotNull Model model) {
        model.addAttribute("timetracking", timeTrackingService.findAll());
        model.addAttribute("peers", peerService.findAll());
        return "timetracking";
    }

    @PostMapping("/timetracking/delete")
    public String delete(Long id) {
        timeTrackingService.delete(id);
        return "redirect:/timetracking";
    }

    @PostMapping("/timetracking/add")
    public String add(TimeTracking timeTracking) {
        timeTrackingService.save(timeTracking);
        return "redirect:/timetracking";
    }

    @GetMapping("updates/timetracking/{id}")
    public String edit(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("timetracking", timeTrackingService.findById(id).get());
        model.addAttribute("peers", peerService.findAll());
        return "updates/timetracking";
    }

    @PostMapping("/timetracking/update")
    public String update(TimeTracking timeTracking) {
        timeTrackingService.update(timeTracking);
        return "redirect:/timetracking";
    }

    @PostMapping(value = "timetracking/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        List<TimeTrackingDTO> timeTrackingListDTO = FileImportExport.importCsv(multipartFile, TimeTrackingDTO.class);
        timeTrackingService.saveAll(timeTrackingMapper.toEntities(timeTrackingListDTO));
        return "redirect:/timetracking";
    }

    @RequestMapping(path = "timetracking/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse,
                timeTrackingMapper.toDTOs(timeTrackingService.findAll()), TimeTrackingDTO.class);
    }
}
