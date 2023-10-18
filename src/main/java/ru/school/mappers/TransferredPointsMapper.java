package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.TransferredPointsDTO;
import ru.school.model.entity.TransferredPoints;
import ru.school.services.PeerService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TransferredPointsMapper {

    private final PeerService peerService;

    public List<TransferredPoints> toEntities(List<TransferredPointsDTO> transferredPointsListDTO) {
        List<TransferredPoints> result = new ArrayList<>();
        for (TransferredPointsDTO it : transferredPointsListDTO) {
            if (peerService.findById(it.getCheckingPeer()).isPresent() &&
                peerService.findById(it.getCheckedPeer()).isPresent()) {

                result.add(new TransferredPoints(
                        it.getId(),
                        peerService.findById(it.getCheckingPeer()).get(),
                        peerService.findById(it.getCheckedPeer()).get(),
                        it.getAmount()));
            }
        }
        return result;
    }

    public List<TransferredPointsDTO> toDTOs(List<TransferredPoints> transferredPointsList) {
        return transferredPointsList.stream().map(x->new TransferredPointsDTO(
                                        x.getId(),
                                        x.getCheckingPeer().getNickname(),
                                        x.getCheckedPeer().getNickname(),
                                        x.getAmount()))
                                        .collect(Collectors.toList());
    }
}
