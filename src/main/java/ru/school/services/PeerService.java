package ru.school.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.school.model.entity.Peer;
import ru.school.repositories.PeerRepository;

import java.util.List;

@Service
public class PeerService extends BaseService<Peer, String> {

    private final PeerRepository peerRepository;

    @Autowired
    protected PeerService(PeerRepository repository) {
        super(repository);
        peerRepository = repository;
    }

    public List<String> getAllNicknames() {
        return peerRepository.findAllNicknames();
    }

    public List<Peer> getAllPeers() {
        return peerRepository.findAll();
    }
}
