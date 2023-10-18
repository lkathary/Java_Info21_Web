package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.PeerToPeerDTO;
import ru.school.model.Status;
import ru.school.model.entity.PeerToPeer;
import ru.school.services.CheckService;
import ru.school.services.PeerService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class PeerToPeerMapper {
    private final PeerService peerService;
    private final CheckService checkService;

    public List<PeerToPeer> toEntities(List<PeerToPeerDTO> peerToPeerListDTO) {
        List<PeerToPeer> result = new ArrayList<>();
        for (PeerToPeerDTO it : peerToPeerListDTO) {
            if (checkService.findById(it.getCheck_id()).isPresent() &&
                    peerService.findById(it.getCheckingPeer()).isPresent()) {

                result.add(new PeerToPeer(
                        it.getId(),
                        checkService.findById(it.getCheck_id()).get(),
                        peerService.findById(it.getCheckingPeer()).get(),
                        Status.valueOf(it.getStatus()),
                        it.getTime()));
            }
        }
        return result;
    }

    public List<PeerToPeerDTO> toDTOs(List<PeerToPeer> peerToPeerList) {
        return peerToPeerList.stream().map(x->new PeerToPeerDTO(
                            x.getId(),
                            x.getCheck_id().getId(),
                            x.getCheckingPeer().getNickname(),
                            x.getStatus().name(),
                            x.getTime()))
                            .collect(Collectors.toList());
    }
}
