package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.TransferredPointsDTO;
import ru.school.mappers.TransferredPointsMapper;
import ru.school.model.entity.TransferredPoints;
import ru.school.services.PeerService;
import ru.school.services.TransferredPointsService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class TransferredPointsController {

    private final TransferredPointsService transferredPointsService;
    private final PeerService peerService;

    private final TransferredPointsMapper transferredPointsMapper;

    @GetMapping("/transferredpoints")
    public String findAll(@NotNull Model model) {
        model.addAttribute("transferredpoints", transferredPointsService.findAll());
        model.addAttribute("peers", peerService.findAll());
        return "transferredpoints";
    }

    @PostMapping("/transferredpoints/delete")
    public String delete(Long id) {
        transferredPointsService.delete(id);
        return "redirect:/transferredpoints";
    }

    @PostMapping("/transferredpoints/add")
    public String add(TransferredPoints transferredPoints) {
        transferredPointsService.save(transferredPoints);
        return "redirect:/transferredpoints";
    }

    @GetMapping("updates/transferredpoints/{id}")
    public String edit(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("transferredpoints", transferredPointsService.findById(id).get());
        model.addAttribute("peers", peerService.findAll());
        return "updates/transferredpoints";
    }

    @PostMapping("/transferredpoints/update")
    public String update(TransferredPoints transferredPoints) {
        transferredPointsService.update(transferredPoints);
        return "redirect:/transferredpoints";
    }

    @PostMapping(value = "transferredpoints/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        List<TransferredPointsDTO> transferredPointsListDTO =
                FileImportExport.importCsv(multipartFile, TransferredPointsDTO.class);
        transferredPointsService.saveAll(transferredPointsMapper.toEntities(transferredPointsListDTO));
        return "redirect:/transferredpoints";
    }

    @RequestMapping(path = "transferredpoints/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse,
                transferredPointsMapper.toDTOs(transferredPointsService.findAll()), TransferredPointsDTO.class);
    }
}
