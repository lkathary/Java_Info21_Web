package ru.school.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.XP;
import ru.school.repositories.XPRepository;

@Service
public class XPService extends BaseService<XP, Long> {

    private final XPRepository xpRepository;

    @Autowired
    public XPService(XPRepository xpRepository) {
        super(xpRepository);
        this.xpRepository = xpRepository;
    }
}
