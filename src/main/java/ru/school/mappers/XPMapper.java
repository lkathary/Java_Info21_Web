package ru.school.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.school.dto.XpDTO;
import ru.school.model.entity.XP;
import ru.school.services.CheckService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class XPMapper {
    private final CheckService checkService;

    public List<XP> toEntities(List<XpDTO> xpListDTO) {
        List<XP> result = new ArrayList<>();
        for (XpDTO it : xpListDTO) {
            if (checkService.findById(it.getCheck_id()).isPresent()) {
                result.add(new XP(
                        it.getId(),
                        checkService.findById(it.getCheck_id()).get(),
                        it.getAmount()));
            }
        }
        return result;
    }

    public List<XpDTO> toDTOs(List<XP> xpList) {
        return xpList.stream().map(x->new XpDTO(
                                        x.getId(),
                                        x.getCheck_id().getId(),
                                        x.getAmount()))
                                        .collect(Collectors.toList());
    }
}
