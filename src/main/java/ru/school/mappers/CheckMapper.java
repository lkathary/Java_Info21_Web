package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.CheckDTO;
import ru.school.model.entity.Check;
import ru.school.services.PeerService;
import ru.school.services.TaskService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CheckMapper {
    private final PeerService peerService;
    private final TaskService taskService;

    public List<Check> toEntities(List<CheckDTO> checkListDTO) {
        List<Check> result = new ArrayList<>();
        for (CheckDTO it : checkListDTO) {
            if (peerService.findById(it.getPeer()).isPresent() && taskService.findById(it.getTask()).isPresent()) {
                result.add(new Check(
                        it.getId(),
                        peerService.findById(it.getPeer()).get(),
                        taskService.findById(it.getTask()).get(),
                        it.getDate()));
            }
        }
        return result;
    }

    public List<CheckDTO> toDTOs(List<Check> checkList) {
        return checkList.stream().map(x->new CheckDTO(
                                        x.getId(),
                                        x.getPeer().getNickname(),
                                        x.getTask().getTitle(),
                                        x.getDate())).collect(Collectors.toList());
    }
}
