package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.FriendsDTO;
import ru.school.model.entity.Friends;
import ru.school.services.PeerService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FriendsMapper {

    private final PeerService peerService;

    public List<Friends> toEntities(List<FriendsDTO> friendsListDTO) {
        List<Friends> result = new ArrayList<>();
        for (FriendsDTO it : friendsListDTO) {
            if (peerService.findById(it.getPeer1()).isPresent() && peerService.findById(it.getPeer2()).isPresent()) {
                result.add(new Friends(
                        it.getId(),
                        peerService.findById(it.getPeer1()).get(),
                        peerService.findById(it.getPeer2()).get()));
            }
        }
        return result;
    }

    public List<FriendsDTO> toDTOs(List<Friends> friendsList) {
        return friendsList.stream().map(x->new FriendsDTO(
                                        x.getId(),
                                        x.getPeer1().getNickname(),
                                        x.getPeer2().getNickname()))
                                        .collect(Collectors.toList());
    }
}
