package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.PeerDTO;
import ru.school.model.entity.Peer;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PeerMapper {
    public List<Peer> toEntities(List<PeerDTO> peerListDTO) {
        return peerListDTO.stream().map(x -> new Peer(x.getNickname(), x.getBirthday())).collect(Collectors.toList());
    }

    public List<PeerDTO> toDTOs(List<Peer> peerList) {
        return peerList.stream().map(x->new PeerDTO(x.getNickname(),x.getBirthday())).collect(Collectors.toList());
    }
}
