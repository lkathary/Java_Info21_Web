package ru.school.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.PeerToPeer;
import ru.school.repositories.PeerToPeerRepository;

@Service
public class PeerToPeerService extends BaseService<PeerToPeer, Long> {

    private final ru.school.repositories.PeerToPeerRepository peerToPeerRepository;

    @Autowired
    public PeerToPeerService(PeerToPeerRepository peerToPeerRepository) {
        super(peerToPeerRepository);
        this.peerToPeerRepository = peerToPeerRepository;
    }
}
