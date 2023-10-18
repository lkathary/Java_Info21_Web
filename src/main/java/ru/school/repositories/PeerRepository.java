package ru.school.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.school.model.entity.Peer;

import java.util.List;
import java.util.Optional;

@Repository
public interface PeerRepository extends JpaRepository<Peer, String> {
    Optional<Peer> findPeerByNickname(String nickname);

    @Query(value = "select nickname from peers",
            nativeQuery = true)
    List<String> findAllNicknames();
}
