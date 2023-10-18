package ru.school.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.Verter;
import ru.school.repositories.VerterRepository;

@Service
public class VerterService extends BaseService<Verter, Long> {

    private final VerterRepository verterRepository;

    @Autowired
    public VerterService(VerterRepository verterRepository) {
        super(verterRepository);
        this.verterRepository = verterRepository;
    }
}
