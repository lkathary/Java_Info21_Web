package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.VerterDTO;
import ru.school.model.Status;
import ru.school.model.entity.Verter;
import ru.school.services.CheckService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class VerterMapper {
    private final CheckService checkService;

    public List<Verter> toEntities(List<VerterDTO> verterToPeerListDTO) {
        List<Verter> result = new ArrayList<>();
        for (VerterDTO it : verterToPeerListDTO) {
            if (checkService.findById(it.getCheck_id()).isPresent()) {

                result.add(new Verter(
                        it.getId(),
                        checkService.findById(it.getCheck_id()).get(),
                        Status.valueOf(it.getStatus()),
                        it.getTime()));
            }
        }
        return result;
    }

    public List<VerterDTO> toDTOs(List<Verter> verterToPeerList) {
        return verterToPeerList.stream().map(x->new VerterDTO(
                                        x.getId(),
                                        x.getCheck_id().getId(),
                                        x.getStatus().name(),
                                        x.getTime()))
                                        .collect(Collectors.toList());
    }
}
