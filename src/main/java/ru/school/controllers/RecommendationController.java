package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.RecommendationDTO;
import ru.school.mappers.RecommendationMapper;
import ru.school.model.entity.Recommendation;
import ru.school.services.PeerService;
import ru.school.services.RecommendationService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final PeerService peerService;

    private final RecommendationMapper recommendationMapper;

    @GetMapping("/recommendation")
    public String findAll(@NotNull Model model) {
        model.addAttribute("recommendation", recommendationService.findAll());
        model.addAttribute("peers", peerService.findAll());
        return "recommendation";
    }

    @PostMapping("/recommendation/delete")
    public String delete(Long id) {
        recommendationService.delete(id);
        return "redirect:/recommendation";
    }

    @PostMapping("/recommendation/add")
    public String add(Recommendation recommendation) {
        recommendationService.save(recommendation);
        return "redirect:/recommendation";
    }

    @GetMapping("updates/recommendation/{id}")
    public String edit(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("recommendation", recommendationService.findById(id).get());
        model.addAttribute("peers", peerService.findAll());
        return "updates/recommendation";
    }

    @PostMapping("/recommendation/update")
    public String update(Recommendation recommendation) {
        recommendationService.update(recommendation);
        return "redirect:/recommendation";
    }


    @PostMapping(value = "recommendation/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        List<RecommendationDTO> recommendationListDTO =
                FileImportExport.importCsv(multipartFile, RecommendationDTO.class);
        recommendationService.saveAll(recommendationMapper.toEntities(recommendationListDTO));
        return "redirect:/recommendation";
    }

    @RequestMapping(path = "recommendation/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse,
                recommendationMapper.toDTOs(recommendationService.findAll()), RecommendationDTO.class);
    }
}
