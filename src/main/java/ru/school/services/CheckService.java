package ru.school.services;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.Check;
import ru.school.repositories.CheckRepository;

import java.util.List;

@Service
@Slf4j
public class CheckService extends BaseService<Check, Long> {

    private final CheckRepository checkRepository;

    @Autowired
    public CheckService(CheckRepository repository) {
        super(repository);
        this.checkRepository = repository;
    }

    public List<Long> getAllIds() {
        return checkRepository.findAllIdsAsc();
    }

    public List<Check> getAllChecks() {
        return checkRepository.findAll();
    }

}
