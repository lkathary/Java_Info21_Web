package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.PeerDTO;
import ru.school.mappers.PeerMapper;
import ru.school.model.entity.Peer;
import ru.school.services.PeerService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class PeerController {

    private final PeerService peerService;

    private final PeerMapper peerMapper;

    @GetMapping("/peers")
    public String findAll(@NotNull Model model) {
        model.addAttribute("peers", peerService.getAllPeers());
        return "peers";
    }

    @PostMapping("/peers/delete")
    public String delete(String nickname) {
        peerService.delete(nickname);
        return "redirect:/peers";
    }

    @PostMapping("/peers/add")
    public String add(Peer peer) {
//        System.out.println("=========" + peer.toString());
        peerService.save(peer);
        return "redirect:/peers";
    }

    @GetMapping("updates/peer/{id}")
    public String edit(@PathVariable String id, @NotNull Model model) {
//        System.out.println("=========" + id + " -> " + peerService.findById(id));
        model.addAttribute("peer", peerService.findById(id).get());
        return "updates/peer";
    }

    @PostMapping("/peers/update")
    public String update(Peer peer) {
        peerService.update(peer);
        return "redirect:/peers";
    }

    @PostMapping(value = "peers/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        List<PeerDTO> peerListDTO = FileImportExport.importCsv(multipartFile, PeerDTO.class);
        peerService.saveAll(peerMapper.toEntities(peerListDTO));
        return "redirect:/peers";
    }

    @RequestMapping(path = "peers/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse, peerMapper.toDTOs(peerService.findAll()), PeerDTO.class);
    }
}
