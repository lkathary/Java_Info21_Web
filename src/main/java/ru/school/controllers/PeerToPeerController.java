package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.PeerToPeerDTO;
import ru.school.mappers.PeerToPeerMapper;
import ru.school.model.entity.PeerToPeer;
import ru.school.services.CheckService;
import ru.school.services.PeerService;
import ru.school.services.PeerToPeerService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PeerToPeerController {

    private final PeerToPeerService peerToPeerService;
    private final CheckService checkService;
    private final PeerService peerService;

    private final PeerToPeerMapper peerToPeerMapper;

    @GetMapping("/peertopeer")
    public String findAll(@NotNull Model model) {
        model.addAttribute("peertopeer", peerToPeerService.findAll());
        model.addAttribute("checks", checkService.findAll());
        model.addAttribute("peers", peerService.getAllPeers());
        return "peertopeer";
    }

    @PostMapping("/peertopeer/delete")
    public String delete(Long id) {
        peerToPeerService.delete(id);
        return "redirect:/peertopeer";
    }

    @PostMapping("/peertopeer/add")
    public String add(PeerToPeer peerToPeer) {
        peerToPeerService.save(peerToPeer);
        return "redirect:/peertopeer";
    }

    @GetMapping("updates/peertopeer/{id}")
    public String edit(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("peertopeer", peerToPeerService.findById(id).get());
        model.addAttribute("checks", checkService.findAll());
        model.addAttribute("peers", peerService.getAllPeers());
        return "updates/peertopeer";
    }

    @PostMapping("/peertopeer/update")
    public String update(PeerToPeer peerToPee) {
        peerToPeerService.update(peerToPee);
        return "redirect:/peertopeer";
    }

    @PostMapping(value = "peertopeer/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        List<PeerToPeerDTO> peerToPeerListDTO = FileImportExport.importCsv(multipartFile, PeerToPeerDTO.class);
        peerToPeerService.saveAll(peerToPeerMapper.toEntities(peerToPeerListDTO));
        return "redirect:/peertopeer";
    }

    @RequestMapping(path = "peertopeer/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse,
                peerToPeerMapper.toDTOs(peerToPeerService.findAll()), PeerToPeerDTO.class);
    }
}
