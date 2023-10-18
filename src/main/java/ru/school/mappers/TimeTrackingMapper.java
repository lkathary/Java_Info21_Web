package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.TimeTrackingDTO;
import ru.school.model.entity.TimeTracking;
import ru.school.services.PeerService;
import ru.school.services.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class TimeTrackingMapper {
    private final PeerService peerService;
    private final TaskService taskService;

    public List<TimeTracking> toEntities(List<TimeTrackingDTO> timeTrackingListDTO) {
        List<TimeTracking> result = new ArrayList<>();
        for (TimeTrackingDTO it : timeTrackingListDTO) {
            if (peerService.findById(it.getPeer()).isPresent()) {
                result.add(new TimeTracking(
                        it.getId(),
                        peerService.findById(it.getPeer()).get(),
                        it.getDate(),
                        it.getTime(),
                        it.getState()));
            }
        }
        return result;
    }

    public List<TimeTrackingDTO> toDTOs(List<TimeTracking> timeTrackingList) {
        return timeTrackingList.stream().map(x->new TimeTrackingDTO(
                                        x.getId(),
                                        x.getPeer().getNickname(),
                                        x.getDate(),
                                        x.getTime(),
                                        x.getStatus()))
                                        .collect(Collectors.toList());
    }
}
