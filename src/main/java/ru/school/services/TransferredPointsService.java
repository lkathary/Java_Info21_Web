package ru.school.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.TransferredPoints;
import ru.school.repositories.TransferredPointsRepository;

@Service
public class TransferredPointsService extends BaseService<TransferredPoints, Long> {

    private final TransferredPointsRepository transferredPointsRepository;

    @Autowired
    public TransferredPointsService(TransferredPointsRepository transferredPointsRepository) {
        super(transferredPointsRepository);
        this.transferredPointsRepository = transferredPointsRepository;
    }
}
