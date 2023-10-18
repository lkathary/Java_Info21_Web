package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.RecommendationDTO;
import ru.school.model.entity.Recommendation;
import ru.school.services.PeerService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class RecommendationMapper {

    private final PeerService peerService;

    public List<Recommendation> toEntities(List<RecommendationDTO> recommendationListDTO) {
        List<Recommendation> result = new ArrayList<>();
        for (RecommendationDTO it : recommendationListDTO) {
            if (peerService.findById(it.getPeer()).isPresent() &&
                peerService.findById(it.getRecommendedPeer()).isPresent()) {

                result.add(new Recommendation(
                        it.getId(),
                        peerService.findById(it.getPeer()).get(),
                        peerService.findById(it.getRecommendedPeer()).get()));
            }
        }
        return result;
    }

    public List<RecommendationDTO> toDTOs(List<Recommendation> recommendationList) {
        return recommendationList.stream().map(x->new RecommendationDTO(
                                        x.getId(),
                                        x.getPeer().getNickname(),
                                        x.getRecommendedPeer().getNickname()))
                                        .collect(Collectors.toList());
    }
}
