package ru.school.controllers;

import com.sun.istack.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.school.csv.FileImportExport;
import ru.school.dto.FriendsDTO;
import ru.school.mappers.FriendsMapper;
import ru.school.model.entity.Friends;
import ru.school.services.FriendsService;
import ru.school.services.PeerService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class FriendsController {

    private final FriendsService friendsService;
    private final PeerService peerService;

    private final FriendsMapper friendsMapper;

    @GetMapping("/friends")
    public String findAll(@NotNull Model model) {
        model.addAttribute("friends", friendsService.findAll());
        model.addAttribute("peers", peerService.findAll());
        return "friends";
    }

    @PostMapping("/friends/delete")
    public String delete(Long id) {
        friendsService.delete(id);
        return "redirect:/friends";
    }

    @PostMapping("/friends/add")
    public String add(Friends friends) {
        friendsService.save(friends);
        return "redirect:/friends";
    }

    @GetMapping("updates/friends/{id}")
    public String edit(@PathVariable Long id, @NotNull Model model) {
        model.addAttribute("friends", friendsService.findById(id).get());
        model.addAttribute("peers", peerService.findAll());
        return "updates/friends";
    }

    @PostMapping("/friends/update")
    public String update(Friends friends) {
        friendsService.update(friends);
        return "redirect:/friends";
    }

    @PostMapping(value = "friends/upload")
    public String uploadFile(@RequestParam("file") MultipartFile multipartFile) {
        List<FriendsDTO> friendsListDTO = FileImportExport.importCsv(multipartFile, FriendsDTO.class);
        friendsService.saveAll(friendsMapper.toEntities(friendsListDTO));
        return "redirect:/friends";
    }

    @RequestMapping(path = "friends/unload")
    public void unloadFile(HttpServletResponse servletResponse) {
        FileImportExport.exportCsv(servletResponse, friendsMapper.toDTOs(friendsService.findAll()), FriendsDTO.class);
    }
}
